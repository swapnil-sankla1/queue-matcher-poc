/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.domain

import com.smarsh.queuematcherpoc.domain.SurveillanceContext.PolicyExecutionResult
import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessor
import org.springframework.stereotype.Component

@Component
class PolicyExecutor {
    private var cache: MutableMap<String, Boolean> = mutableMapOf()

    fun evictCache() {
        cache = mutableMapOf()
    }

    fun execute(communication: Communication, policy: Policy, regexProcessor: RegexProcessor, hook: (PolicyExecutionResult) -> Unit): Boolean {
        return cache.computeIfAbsent(communication.gcid + "_" + policy.name()) { policy.apply(communication, regexProcessor, hook) }
    }
}