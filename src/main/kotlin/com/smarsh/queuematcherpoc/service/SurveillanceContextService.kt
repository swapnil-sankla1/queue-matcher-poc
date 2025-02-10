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
                "principal-queue1",
                "principal",
                listOf(
                    SurveillanceContext.IgnorePolicy("principal-queue1", "principal", "ignore1", listOf("hr@smarsh.com")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("principal-queue1", "principal", "filter1", listOf(
                        "I FOLLOWEDBY,1 started FOLLOWEDBY,2 business NEAR,0 part time",
                        "CASH FOLLOWEDBY,3 update AND NEAR,7 security | bid | ask",
                        "trading FOLLOWEDBY,9 coupon AND NEAR,7 tranche | maturity | floor | size",
                        "CIBC | M&T | King | RJ | Citi | PNC | BOK | ING | Key | Barclays | Vulcan | USBI | HCW | Stanley | Mizuho | Seelaus | Goldman | GS | JPM | Morgan | JPMorgan | MS | BofA | Fifth | FHN | Agricole | WauBank | Capital | DCM | Comerica | Truist | Citizens | MUFG | Wells | RBC | Ryan | Academy | SWS | USB | Scotia | BNPP | ASIF | DB | LCM | SG | Natixis NEAR,4 signed FOLLOWEDBY,1 off",
//                        "This email originated from a source outside FOLLOWEDBY,1 Principal network",
                        "Avenue FOLLOWEDBY,2 Americas"
                    ), Scenario("scenario1")),
                )
            ),
            SurveillanceContext(
                "jpmc-queue1",
                "jpmc",
                listOf(
                    SurveillanceContext.IgnorePolicy("jpmc-queue1", "jpmc", "jp-ignore1", listOf("hr@smarsh.com")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("jpmc-queue1", "jpmc", "jp-filter1", listOf(
                        "I FOLLOWEDBY,1 started FOLLOWEDBY,2 business NEAR,0 part time",
                        "CASH FOLLOWEDBY,3 update AND NEAR,7 security | bid | ask",
                        "trading FOLLOWEDBY,9 coupon AND NEAR,7 tranche | maturity | floor | size",
                        "CIBC | M&T | King | RJ | Citi | PNC | BOK | ING | Key | Barclays | Vulcan | USBI | HCW | Stanley | Mizuho | Seelaus | Goldman | GS | JPM | Morgan | JPMorgan | MS | BofA | Fifth | FHN | Agricole | WauBank | Capital | DCM | Comerica | Truist | Citizens | MUFG | Wells | RBC | Ryan | Academy | SWS | USB | Scotia | BNPP | ASIF | DB | LCM | SG | Natixis NEAR,4 signed FOLLOWEDBY,1 off",
                        "Support Team NEAR,0 PTP Development",
                    ), Scenario("scenario1")),
                )
            ),
            SurveillanceContext(
                "jpmc-queue2",
                "jpmc",
                listOf(
                    SurveillanceContext.IgnorePolicy("jpmc-queue2", "jpmc", "jp-ignore1", listOf("hr@smarsh.com")),
                ),
                listOf(
                    SurveillanceContext.FilterPolicy("jpmc-queue2", "jpmc", "jp-filter2", listOf(
                        "I FOLLOWEDBY,1 started FOLLOWEDBY,2 business NEAR,0 part time",
                        "CASH FOLLOWEDBY,3 update AND NEAR,7 security | bid | ask",
                        "trading FOLLOWEDBY,9 coupon AND NEAR,7 tranche | maturity | floor | size",
                        "CIBC | M&T | King | RJ | Citi | PNC | BOK | ING | Key | Barclays | Vulcan | USBI | HCW | Stanley | Mizuho | Seelaus | Goldman | GS | JPM | Morgan | JPMorgan | MS | BofA | Fifth | FHN | Agricole | WauBank | Capital | DCM | Comerica | Truist | Citizens | MUFG | Wells | RBC | Ryan | Academy | SWS | USB | Scotia | BNPP | ASIF | DB | LCM | SG | Natixis NEAR,4 signed FOLLOWEDBY,1 off",
                        "restricted.chase.com",
                        "Reconciliation NEAR,0 Results Viewer"
                    ), Scenario("scenario1")),
                )
            )
        )
    }
}