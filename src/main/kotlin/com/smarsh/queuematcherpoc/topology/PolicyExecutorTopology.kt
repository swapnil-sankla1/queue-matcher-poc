package com.smarsh.queuematcherpoc.topology

import com.fasterxml.jackson.databind.ObjectMapper
import com.smarsh.queuematcherpoc.domain.Communication
import com.smarsh.queuematcherpoc.domain.SurveillanceContext
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
import org.springframework.stereotype.Component

/*
* Copyright 2025 Smarsh Inc.
*/
@Component
class PolicyExecutorTopology(
    val objectMapper: ObjectMapper,
    val surveillanceContextService: SurveillanceContextService,
    val customSerde: CustomSerde
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
            .peek { k, v -> logger.debug("message received on ingestion event topic. Key: {}. Value: {}", k, v) }
            .mapValues { v -> objectMapper.readValue(v, Communication::class.java) }
            .flatMapValues(::getEligibleSurveillanceContext)
            .filter { _, v -> v.surveillanceContext.ignorePolicies.none { policy -> policy.apply(v.communication) } }
            .filter { _, v -> v.surveillanceContext.filterPolicies.all { policy -> policy.apply(v.communication) } }
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

    data class CommunicationWithApplicableSurveillanceContext(val communication: Communication, val surveillanceContext: SurveillanceContext)
}
