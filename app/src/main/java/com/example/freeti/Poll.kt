package com.example.freeti

data class Option(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    var votes: Int = 0
)

data class Poll(
    val id: String = java.util.UUID.randomUUID().toString(),
    val question: String,
    val options: MutableList<Option> = mutableListOf()
)