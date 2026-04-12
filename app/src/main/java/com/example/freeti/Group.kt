package com.example.freeti

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val memberCount: Int = 0,
    val imageResId: Int = R.drawable.ic_group_placeholder
)