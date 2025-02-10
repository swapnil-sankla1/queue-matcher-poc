package com.smarsh.queuematcherpoc.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class RawCommunication(val gcid: String, val texts: Texts) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    data class Texts(val user: List<Content>, val sys: List<Content>) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        data class Content(val content: String?)
    }

    fun content(): String {
        val textUnderUser = texts.user.filter { !it.content.isNullOrBlank() }.joinToString("\n")
        val textUnderSys = texts.sys.filter { !it.content.isNullOrBlank() }.joinToString("\n")
        return textUnderUser + textUnderSys
    }
}