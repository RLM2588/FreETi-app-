package com.example.freeti.network_entity

data class UpdateResponse(
    val status: String,       // "ok", "update", "critical"
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotes: String? = null
)