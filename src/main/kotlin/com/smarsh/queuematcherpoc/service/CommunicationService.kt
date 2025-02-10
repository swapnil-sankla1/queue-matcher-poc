package com.smarsh.queuematcherpoc.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.smarsh.queuematcherpoc.domain.Communication
import com.smarsh.queuematcherpoc.domain.CommunicationNotificationEvent
import com.smarsh.queuematcherpoc.domain.RawCommunication
import org.springframework.stereotype.Service
import software.amazon.awssdk.core.async.AsyncResponseTransformer
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetObjectRequest

@Service
class CommunicationService(private val s3Client: S3AsyncClient, private val objectMapper: ObjectMapper) {
    fun retrieve(communicationNotificationEvent: CommunicationNotificationEvent): Communication {
        val request = GetObjectRequest
            .builder()
            .bucket(communicationNotificationEvent.bucketName)
            .key(communicationNotificationEvent.bucketName + communicationNotificationEvent.absoluteFileName)
            .build()

        //TODO: Can this be simplified to use normal s3 client given here as well we are calling join() which will block the thread (green thread)
        val rawCommunication =
            objectMapper.readValue(s3Client.getObject(request, AsyncResponseTransformer.toBytes()).join().asUtf8String(), RawCommunication::class.java)
        return Communication(communicationNotificationEvent.gcid, communicationNotificationEvent.tenantId, rawCommunication.content())
    }
}
