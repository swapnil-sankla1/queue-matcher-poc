package com.smarsh.queuematcherpoc.domain

data class SurveillanceContext(val name: String, val tenantId: String, val ignorePolicies: List<IgnorePolicy>, val filterPolicies: List<FilterPolicy>) {
    data class IgnorePolicy(val name: String, val regexPatterns: List<String>) {
        fun apply(communication: Communication): Boolean {
            //TODO: Fix this return false
            return false
        }
    }

    data class FilterPolicy(val name: String, val regexPatterns: List<String>, val scenarios: Scenario) {
        data class Scenario(val name: String)

        fun apply(communication: Communication): Boolean {
            //TODO: Fix this
            return true
        }
    }
}