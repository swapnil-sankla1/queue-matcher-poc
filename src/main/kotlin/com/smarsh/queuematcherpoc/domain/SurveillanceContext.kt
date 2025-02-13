package com.smarsh.queuematcherpoc.domain

import com.smarsh.queuematcherpoc.domain.SurveillanceContext.PolicyExecutionResult
import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessor
import java.util.Date

data class SurveillanceContext(
    val name: String,
    val tenantId: String,
    val ignorePolicies: List<Policy>,
    val filterPolicies: List<Policy>
) {
    data class IgnorePolicy(val queueName: String, val tenantId: String, val name: String, val regexPatterns: List<String>) : Policy {
        override fun apply(communication: Communication, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean {
            val result = regexProcessor.registerMatchers(regexPatterns).match(communication.content)
            hook.invoke(buildPolicyExecutionResult(result, communication))
            return result
        }

        override fun name() = name

        private fun buildPolicyExecutionResult(result: Boolean, communication: Communication): PolicyExecutionResult {
            return if (result)
                PolicyExecutionResult(
                    """
                    ${Date()}: $queueName configured on tenantId: $tenantId ignored document with gcid: ${communication.gcid} as part of ignore policy: $name
                    """.trimIndent()
                )
            else
                PolicyExecutionResult(
                    """
                    ${Date()}: $queueName configured on tenantId: $tenantId did not ignore document with gcid: ${communication.gcid} as part of ignore policy: $name
                    """.trimIndent()
                )
        }
    }

    data class FilterPolicy(val queueName: String, val tenantId: String, val name: String, val regexPatterns: List<String>, val scenario: Scenario) : Policy {
        data class Scenario(val name: String)

        override fun apply(communication: Communication, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean {
            val result = regexProcessor.registerMatchers(regexPatterns).match(communication.content)
            hook.invoke(buildPolicyExecutionResult(result, communication))
            return result
        }

        override fun name() = name

        override fun scenario() = scenario.name

        private fun buildPolicyExecutionResult(result: Boolean, communication: Communication): PolicyExecutionResult {
            return if (result) {
                PolicyExecutionResult(
                    """
                    ${Date()}: $queueName configured on tenantId: $tenantId filtered document with gcid: ${communication.gcid} as part of filter policy: $name
                    """.trimIndent()
                )
            } else {
                PolicyExecutionResult(
                    """
                    ${Date()}: $queueName configured on tenantId: $tenantId did not filter document with gcid: ${communication.gcid} as part of filter policy: $name
                    """.trimIndent()
                )
            }
        }
    }

    data class PolicyExecutionResult(val content: String)
}

interface Policy {
    fun apply(communication: Communication, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean
    fun name(): String
    fun scenario(): String {
        TODO("Not yet implemented")
    }
}