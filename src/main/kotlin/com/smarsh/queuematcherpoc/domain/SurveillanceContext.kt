package com.smarsh.queuematcherpoc.domain

import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessor

data class SurveillanceContext(
    val name: String,
    val tenantId: String,
    val ignorePolicies: List<IgnorePolicy>,
    val filterPolicies: List<FilterPolicy>
) {
    data class IgnorePolicy(val queueName: String, val tenantId: String, val name: String, val regexPatterns: List<String>) {
        fun apply(communication: Communication, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean {
            val result = regexProcessor.registerMatchers(regexPatterns).match(communication.content)
            hook.invoke(buildPolicyExecutionResult(result, communication))
            return result
        }

        private fun buildPolicyExecutionResult(result: Boolean, communication: Communication): PolicyExecutionResult {
            return if (result)
                PolicyExecutionResult(
                    """
                    $queueName configured on tenantId: $tenantId ignored document with gcid: ${communication.gcid} as part of ignore policy: $name
                    """.trimIndent()
                )
            else
                PolicyExecutionResult(
                    """
                    $queueName configured on tenantId: $tenantId did not ignore document with gcid: ${communication.gcid} as part of ignore policy: $name
                    """.trimIndent()
                )
        }
    }

    data class FilterPolicy(val queueName: String, val tenantId: String, val name: String, val regexPatterns: List<String>, val scenarios: Scenario) {
        data class Scenario(val name: String)

        fun apply(communication: Communication, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean {
            val result = regexProcessor.registerMatchers(regexPatterns).match(communication.content)
            hook.invoke(buildPolicyExecutionResult(result, communication))
            return result
        }

        private fun buildPolicyExecutionResult(result: Boolean, communication: Communication): PolicyExecutionResult {
            return if (result) {
                PolicyExecutionResult(
                    """
                    $queueName configured on tenantId: $tenantId filtered document with gcid: ${communication.gcid} as part of filter policy: $name
                    """.trimIndent()
                )
            } else {
                PolicyExecutionResult(
                    """
                    $queueName configured on tenantId: $tenantId did not filter document with gcid: ${communication.gcid} as part of filter policy: $name
                    """.trimIndent()
                )
            }
        }
    }

    data class PolicyExecutionResult(val content: String)
}