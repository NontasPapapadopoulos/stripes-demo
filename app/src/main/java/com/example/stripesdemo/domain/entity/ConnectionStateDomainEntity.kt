package com.example.stripesdemo.domain.entity

data class ConnectionStateDomainEntity(
    val deviceId: String,
    val state: String,
    val deviceName: String?
)

