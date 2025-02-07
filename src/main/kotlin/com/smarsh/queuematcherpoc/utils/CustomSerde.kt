/*
* Copyright 2025 Smarsh Inc.
*/

package com.smarsh.queuematcherpoc.utils

import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.common.serialization.Serdes
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component

@Component
class CustomSerde {
    fun <T> get(clazz: Class<T>): Serde<T> {
        return Serdes.serdeFrom(JsonSerializer(), JsonDeserializer(clazz))
    }
}