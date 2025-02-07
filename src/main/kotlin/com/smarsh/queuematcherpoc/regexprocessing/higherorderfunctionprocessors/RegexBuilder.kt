/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors

import com.smarsh.queuematcherpoc.regexprocessing.higherorderfunctionprocessors.Constants.OR_EXPRESSION
import org.springframework.stereotype.Component
import java.util.*

@Component
class RegexBuilder(
    private val higherOrderFunctionProcessorFactory: HigherOrderFunctionProcessorFactory,
    private val orProcessor: OrProcessor
) {
    private val space = " "

    fun build(pattern: String): String {
        if (!higherOrderFunctionProcessorFactory.matchesAnyProcessor(pattern)) {
            return pattern
        }
        return processExpressionTree(processOrs(buildExpressionTree(formatOrExpression(pattern))))
    }

    private fun formatOrExpression(pattern: String): String {
        if (!pattern.contains(OR_EXPRESSION)) {
            return pattern
        }
        return pattern.replace("\\s+\\|+\\s+".toRegex(), OR_EXPRESSION)
    }

    private fun buildExpressionTree(pattern: String): Stack<String> {
        val stack = Stack<String>()
        var stringBuilder = StringBuilder()

        val strings = pattern.split(space.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        for (string in strings) {
            if (higherOrderFunctionProcessorFactory.matchesAnyProcessor(string)) {
                stack.push(stringBuilder.toString())
                stringBuilder = StringBuilder()
                stack.push(string)
            } else {
                stringBuilder.append(string)
                stringBuilder.append(space)
            }
        }
        stack.push(stringBuilder.toString())
        return stack
    }

    private fun processOrs(stack: Stack<String>): Stack<String> {
        val newStack = Stack<String>()
        for (string in stack) {
            if (string.contains(OR_EXPRESSION)) {
                newStack.push(orProcessor.process(string))
            } else {
                newStack.push(string)
            }
        }
        return newStack
    }

    private fun processExpressionTree(stack: Stack<String>): String {
        while (stack.size > 1) {
            val rhs = stack.pop().trim { it <= ' ' }
            val func = stack.pop().trim { it <= ' ' }
            val lhs = stack.pop().trim { it <= ' ' }

            stack.push(higherOrderFunctionProcessorFactory.get(func).process(lhs, rhs, distance(func)))
        }
        return stack.pop()
    }

    private fun distance(func: String): Int {
        return func.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()
    }
}