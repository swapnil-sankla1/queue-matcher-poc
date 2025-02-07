/*
 Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.service

import com.smarsh.queuematcherpoc.domain.Communication
import com.smarsh.queuematcherpoc.domain.SurveillanceContext
import com.smarsh.queuematcherpoc.domain.SurveillanceContext.FilterPolicy.Scenario
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SurveillanceContextService {
    private val logger = LoggerFactory.getLogger(SurveillanceContextService::class.java)

    fun getEligibleSurveillanceContext(communication: Communication): List<SurveillanceContext> {
        val result = all().filter { x -> x.tenantId == communication.tenantId }
        logger.debug("Found ${result.size} eligible surveillance contexts")
        return result
    }

    private fun all(): List<SurveillanceContext> {
        //TODO: Move this to DB
        return listOf(
            SurveillanceContext(
                "queue1",
                "tenant1",
                listOf(
                    SurveillanceContext.IgnorePolicy("queue1", "tenant1", "ignore1", listOf("hr@smarsh.com")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("queue1", "tenant1", "filter1", listOf("a"), Scenario("scenario1")),
                    SurveillanceContext.FilterPolicy("queue1", "tenant1", "filter2", listOf("b"), Scenario("scenario2")),
                    SurveillanceContext.FilterPolicy("queue1", "tenant1", "filter3", listOf("c"), Scenario("scenario3"))
                )
            ),
            SurveillanceContext(
                "queue2",
                "tenant1",
                listOf(
                    SurveillanceContext.IgnorePolicy("queue2", "tenant1", "ignore1", listOf("caizin.com")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("queue2", "tenant1", "filter1", listOf("a"), Scenario("scenario1")),
                )
            )
        )
    }
}