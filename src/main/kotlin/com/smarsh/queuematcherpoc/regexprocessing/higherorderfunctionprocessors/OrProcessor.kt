/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

import org.springframework.stereotype.Component

@Component
class OrProcessor {
    fun process(pattern: String): String {
        val strings = pattern.trim { it <= ' ' }.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val stringBuilder = StringBuilder()
        stringBuilder.append("(")
        for (string in strings) {
            stringBuilder.append("\\b\${string}\\b|".replace("\${string}", string))
        }
        stringBuilder.deleteCharAt(stringBuilder.length - 1)
        stringBuilder.append(")")
        return stringBuilder.toString()
    }
}
