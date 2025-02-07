/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.service

import com.smarsh.queuematcherpoc.domain.SurveillanceContext
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class AuditService(val auditProducer: KafkaTemplate<String, String>) {

    companion object {
        const val AUDIT_EVENT_TOPIC_NAME = "policy-execution-audit-topic"
    }

    fun auditPolicyResult(result: SurveillanceContext.PolicyExecutionResult) {
        auditProducer.send(AUDIT_EVENT_TOPIC_NAME, result.content)
    }
}