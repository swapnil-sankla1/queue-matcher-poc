/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing

import com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors.RegexBuilder
import com.smarsh.queuematcherpoc.regexprocessing.vectorscan.VectorscanRegexProcessor
import org.springframework.stereotype.Component

@Component
class RegexProcessorFactory(val regexBuilder: RegexBuilder) {
    fun get(): RegexProcessor {
        // TODO: Place to return various implementations of regex processing
        return VectorscanRegexProcessor(regexBuilder)
    }
}