package com.smarsh.queuematcherpoc.kafka

import com.smarsh.queuematcherpoc.kafka.SurveillanceRequest.QueueToPolicy.Policy


data class SurveillanceRequest(val surveillanceContexts: MutableList<QueueToPolicy> = mutableListOf()) {
    fun add(name: String, filterPolicies: List<com.smarsh.queuematcherpoc.domain.Policy>): SurveillanceRequest {
        surveillanceContexts.add(QueueToPolicy(name, filterPolicies.map { Policy(it.name(), it.scenario()) }))
        return this
    }
    data class QueueToPolicy(val name: String, val policy: List<Policy>) {
        data class Policy(val name: String, val scenario: String)
    }
}