/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

import com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors.Constants.NEAR_EXPRESSION
import org.springframework.stereotype.Component

@Component
class NearProcessor : HigherOrderFunctionProcessor {
    override fun canProcess(functionName: String): Boolean {
        return functionName.contains(NEAR_EXPRESSION)
    }

    override fun process(lhs: String, rhs: String, distance: Int): String {
        val higherOrderFunctionProcessor: HigherOrderFunctionProcessor = FollowedByProcessor()
        return "(" +
                higherOrderFunctionProcessor.process(lhs, rhs, distance) +
                "|" +
                higherOrderFunctionProcessor.process(rhs, lhs, distance) +
                ")"
    }
}
