/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

import org.springframework.stereotype.Component

@Component
class HigherOrderFunctionProcessorFactory(private val processors: List<HigherOrderFunctionProcessor>) {
    fun get(functionName: String): HigherOrderFunctionProcessor {
        return processors.first { p: HigherOrderFunctionProcessor -> p.canProcess(functionName) }
    }

    fun matchesAnyProcessor(functionName: String): Boolean {
        return processors.any { p: HigherOrderFunctionProcessor -> p.canProcess(functionName) }
    }
}