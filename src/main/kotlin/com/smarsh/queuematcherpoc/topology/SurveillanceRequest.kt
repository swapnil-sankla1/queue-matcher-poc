package com.smarsh.queuematcherpoc.topology

import com.smarsh.queuematcherpoc.domain.SurveillanceContext

data class SurveillanceRequest(val surveillanceContexts: MutableList<Pair<String, List<SurveillanceContext.FilterPolicy>>> = mutableListOf()) {
    fun add(name: String, filterPolicies: List<SurveillanceContext.FilterPolicy>): SurveillanceRequest {
        surveillanceContexts.add(name to filterPolicies)
        return this
    }
}