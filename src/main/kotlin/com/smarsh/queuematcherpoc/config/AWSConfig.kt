/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import java.net.URI

@Configuration
class AWSConfig {
    @Value("\${aws.s3.endpoint}")
    private lateinit var s3Endpoint: String

    @Bean
    fun s3AsyncClient(): S3AsyncClient {
        return S3AsyncClient
            .crtBuilder()
            .endpointOverride(URI.create(s3Endpoint))
            .region(Region.AP_NORTHEAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build()
    }
}