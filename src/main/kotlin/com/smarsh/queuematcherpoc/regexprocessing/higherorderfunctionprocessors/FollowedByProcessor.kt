/*
* Copyright 2025 Smarsh Inc.
*/
package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

import com.smarsh.queuematcherpoc.regexprocessing.Utils.isRegex
import com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors.Constants.FOLLOWED_BY_EXPRESSION
import org.springframework.stereotype.Component

@Component
class FollowedByProcessor : HigherOrderFunctionProcessor {
    override fun canProcess(functionName: String): Boolean {
        return functionName.contains(FOLLOWED_BY_EXPRESSION)
    }

    override fun process(lhs: String, rhs: String, distance: Int): String {
        val lhsTemplate = if (isRegex(lhs)) lhs else "\\b\${lhs}\\b"
        val rhsTemplate = if (isRegex(rhs)) rhs else "\\b\${rhs}\\b"
        val distanceTemplate = if (distance > 0) "(?:\\s+\\w+){0,\${distance}}\\s+" else "\\s+"
        return (lhsTemplate + distanceTemplate + rhsTemplate)
            .replace("\${lhs}", lhs)
            .replace("\${rhs}", rhs)
            .replace("\${distance}", distance.toString())
    }
}