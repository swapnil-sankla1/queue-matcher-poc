/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing.vectorscan

import com.gliwka.hyperscan.wrapper.CompileErrorException
import com.gliwka.hyperscan.wrapper.Database
import com.gliwka.hyperscan.wrapper.Expression
import com.gliwka.hyperscan.wrapper.ExpressionFlag
import com.gliwka.hyperscan.wrapper.Scanner
import com.smarsh.queuematcherpoc.regexprocessing.RegexProcessor
import com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors.RegexBuilder
import org.slf4j.LoggerFactory
import java.util.Arrays

class VectorscanRegexProcessor(val regexBuilder: RegexBuilder, private var matchers: MutableList<Expression> = mutableListOf()) : RegexProcessor {
    private val logger = LoggerFactory.getLogger(VectorscanRegexProcessor::class.java)

    override fun registerMatchers(inputMatchers: List<String>): RegexProcessor {
        matchers = Arrays
            .stream(inputMatchers.toTypedArray())
            .map(regexBuilder::build)
            .map { regex -> Expression(regex, ExpressionFlag.CASELESS) }
            .toList()
        return this
    }

    override fun match(document: String): Boolean {
        try {
            Database.compile(matchers).use { db ->
                Scanner().use { scanner ->
                    scanner.allocScratch(db)
                    return scanner.scan(db, document).isNotEmpty()
                }
            }
        } catch (ce: CompileErrorException) {
            logger.error(ce.failedExpression.toString())
        }
        return false
    }
}