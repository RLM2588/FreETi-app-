package com.example.freeti.network_entity

data class NGroupEventSearch (
    val day_start: String,
    val day_end: String,
    val time_start: String,
    val time_end: String,
    val time_pick: Long,
    val importance: Int,
    val title: String,
    val body: String,
    val colour: String = "FFFFFF"
)