/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.smarsh.queuematcherpoc.domain.CommunicationNotificationEvent
import com.smarsh.queuematcherpoc.domain.PolicyExecutor
import com.smarsh.queuematcherpoc.domain.SurveillanceContext
import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessorFactory
import com.smarsh.queuematcherpoc.service.AuditService
import com.smarsh.queuematcherpoc.service.CommunicationService
import com.smarsh.queuematcherpoc.service.SurveillanceContextService
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
@ConditionalOnProperty(prefix = "app", name = ["consumer"], havingValue = "kafka-consumer")
class PolicyExecutionKafkaListener(
    private val objectMapper: ObjectMapper,
    private val auditService: AuditService,
    private val communicationService: CommunicationService,
    private val surveillanceContextService: SurveillanceContextService,
    private val regexProcessorFactory: RegexProcessorFactory,
    private val surveillanceRequestProducer: KafkaTemplate<String, String>,
    private val policyExecutor: PolicyExecutor
) {
    companion object {
        const val INGESTION_EVENT_TOPIC_NAME = "conduct-ingestion-topic"
        const val PERFORM_SURVEILLANCE_COMMAND_TOPIC = "surveillance-command-topic"
    }

    @KafkaListener(topics = [INGESTION_EVENT_TOPIC_NAME], groupId = "normalKafkaConsumer")
    fun listen(message: ConsumerRecord<String, String>) {
        val gcId = message.key()
        auditService.auditMessageIngestion("${Date()}: Message received on ingestion event topic with key: $gcId")
        val communicationNotificationEvent = objectMapper.readValue(message.value(), CommunicationNotificationEvent::class.java)
        val communication = communicationService.retrieve(communicationNotificationEvent)
        val eligibleSurveillanceContexts = surveillanceContextService.getEligibleSurveillanceContext(communication)

        val surveillanceRequest = eligibleSurveillanceContexts
            .filter {
                it.ignorePolicies.none { policy ->
                    policyExecutor.execute(communication, policy, regexProcessorFactory.get(), auditService::auditPolicyResult)
                }
            }.filter {
                it.filterPolicies.all { policy ->
                    policyExecutor.execute(communication, policy, regexProcessorFactory.get(), auditService::auditPolicyResult)
                }
            }.fold(SurveillanceRequest()) { acc: SurveillanceRequest, it: SurveillanceContext ->
                acc.add(it.name, it.filterPolicies)
            }

        surveillanceRequestProducer.send(
            PERFORM_SURVEILLANCE_COMMAND_TOPIC,
            objectMapper.writeValueAsString(surveillanceRequest)
        )
    }

}