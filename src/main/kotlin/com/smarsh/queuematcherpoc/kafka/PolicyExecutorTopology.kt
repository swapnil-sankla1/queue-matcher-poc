package com.smarsh.queuematcherpoc.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.smarsh.queuematcherpoc.domain.Communication
import com.smarsh.queuematcherpoc.domain.CommunicationNotificationEvent
import com.smarsh.queuematcherpoc.domain.PolicyExecutor
import com.smarsh.queuematcherpoc.domain.SurveillanceContext
import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessorFactory
import com.smarsh.queuematcherpoc.service.AuditService
import com.smarsh.queuematcherpoc.service.CommunicationService
import com.smarsh.queuematcherpoc.service.SurveillanceContextService
import com.smarsh.queuematcherpoc.utils.CustomSerde
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.common.serialization.Serdes.StringSerde
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.Materialized
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.state.Stores
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.Date

/*
* Copyright 2025 Smarsh Inc.
*/
@Component
@ConditionalOnProperty(prefix = "app", name = ["consumer"], havingValue = "kafka-streams" )
class PolicyExecutorTopology(
    private val objectMapper: ObjectMapper,
    private val surveillanceContextService: SurveillanceContextService,
    private val communicationService: CommunicationService,
    private val customSerde: CustomSerde,
    private val regexProcessorFactory: RegexProcessorFactory,
    private val auditService: AuditService,
    private val policyExecutor: PolicyExecutor
) {

    private val logger = LoggerFactory.getLogger(PolicyExecutorTopology::class.java)

    companion object {
        const val INGESTION_EVENT_TOPIC_NAME = "conduct-ingestion-topic"
        const val PERFORM_SURVEILLANCE_COMMAND_TOPIC = "surveillance-command-topic"
        const val SURVEILLANCE_REQUEST_STORE = "surveillance-request-store"
    }

    @Autowired
    fun buildPipeline(streamsBuilder: StreamsBuilder) {
        val stringSerde = StringSerde()

        val topology = streamsBuilder
            .stream(INGESTION_EVENT_TOPIC_NAME, Consumed.with(stringSerde, stringSerde))
            .peek { k, _ -> auditService.auditMessageIngestion("${Date()}: Message received on ingestion event topic with key: $k") }
            .mapValues { v -> objectMapper.readValue(v, CommunicationNotificationEvent::class.java) }
            .mapValues { v -> communicationService.retrieve(v) }
            .peek { k, _ -> auditService.auditMessageIngestion("${Date()}: Communication is downloaded from S3 with key: $k")}
            .flatMapValues(::getEligibleSurveillanceContext)
            .filter { _, v ->
                v.surveillanceContext.ignorePolicies.none { policy ->
                    policyExecutor.execute(v.communication, policy, regexProcessorFactory.get(), auditService::auditPolicyResult)
                }
            }
            .filter { _, v ->
                v.surveillanceContext.filterPolicies.all { policy ->
                    policyExecutor.execute(v.communication, policy, regexProcessorFactory.get(), auditService::auditPolicyResult)
                }
            }
            .peek { k, v -> logger.debug("message generated post applying ignore and filter policy. Key: {}. Value: {}", k, v) }
            .groupByKey()
            .aggregate(
                { SurveillanceRequest() }, //TODO: Fix requestId
                { _, value, aggregate -> aggregate.add(value.surveillanceContext.name, value.surveillanceContext.filterPolicies) },
                Materialized.`as`<String, SurveillanceRequest>(Stores.inMemoryKeyValueStore(SURVEILLANCE_REQUEST_STORE))
                    .withKeySerde(stringSerde)
                    .withValueSerde(customSerde.get(SurveillanceRequest::class.java))
            )

        topology.toStream().to(PERFORM_SURVEILLANCE_COMMAND_TOPIC, Produced.with(Serdes.String(), customSerde.get(SurveillanceRequest::class.java)))
    }

    /**
     * Figures out the eligible queues for given message based on Queue configuration`.
     */
    private fun getEligibleSurveillanceContext(communication: Communication): List<CommunicationWithApplicableSurveillanceContext> {
        return surveillanceContextService
            .getEligibleSurveillanceContext(communication)
            .map { CommunicationWithApplicableSurveillanceContext(communication, it) }
    }

    data class CommunicationWithApplicableSurveillanceContext(
        val communication: Communication,
        val surveillanceContext: SurveillanceContext
    )
}
