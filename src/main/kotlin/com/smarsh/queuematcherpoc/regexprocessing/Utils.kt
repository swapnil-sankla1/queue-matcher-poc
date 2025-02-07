/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing

object Utils {
    fun isRegex(pattern: String): Boolean {
        return pattern.contains("\\b") //TODO: Fix this
    }
}