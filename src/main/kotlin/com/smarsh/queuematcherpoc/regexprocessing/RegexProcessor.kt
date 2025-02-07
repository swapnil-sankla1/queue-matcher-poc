/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing

interface RegexProcessor {
    fun registerMatchers(inputMatchers: List<String>): RegexProcessor
    fun match(document: String): Boolean
}