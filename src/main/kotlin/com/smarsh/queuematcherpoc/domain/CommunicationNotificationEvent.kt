package com.smarsh.queuematcherpoc.domain

data class CommunicationNotificationEvent(
    val gcid: String,
    val tenantId: String,
    val bucketName: String,
    val absoluteFileName: String
)