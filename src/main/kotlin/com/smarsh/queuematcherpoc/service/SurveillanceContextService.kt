/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.service

import com.smarsh.queuematcherpoc.domain.Communication
import com.smarsh.queuematcherpoc.domain.SurveillanceContext
import org.springframework.stereotype.Service

@Service
class SurveillanceContextService {
    fun getEligibleSurveillanceContext(communication: Communication): List<SurveillanceContext> {
        return all().filter { x -> x.tenantId == communication.tenantId }
    }

    private fun all(): List<SurveillanceContext> {
        //TODO: Move this to DB
        return listOf(
            SurveillanceContext(
                "queue1",
                "tenant1",
                listOf(
                    SurveillanceContext.IgnorePolicy("ignore1", listOf("*smarsh*")),
                    SurveillanceContext.IgnorePolicy("ignore2", listOf("*hr*"))
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("filter1", listOf("*a*"), SurveillanceContext.FilterPolicy.Scenario("scenario1")),
                    SurveillanceContext.FilterPolicy("filter2", listOf("*b*"), SurveillanceContext.FilterPolicy.Scenario("scenario2")),
                    SurveillanceContext.FilterPolicy("filter3", listOf("*c*"), SurveillanceContext.FilterPolicy.Scenario("scenario3"))
                )
            ),
            SurveillanceContext(
                "queue2",
                "tenant1",
                listOf(
                    SurveillanceContext.IgnorePolicy("ignore1", listOf("*smarsh*")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("filter1", listOf("*a*"), SurveillanceContext.FilterPolicy.Scenario("scenario1")),
                )
            )
        )
    }
}