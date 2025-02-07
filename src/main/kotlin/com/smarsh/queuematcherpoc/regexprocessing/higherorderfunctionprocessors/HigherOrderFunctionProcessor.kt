/*
* Copyright 2025 Smarsh Inc.
*/
package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

interface HigherOrderFunctionProcessor {
    fun canProcess(functionName: String): Boolean
    fun process(lhs: String, rhs: String, distance: Int): String
}