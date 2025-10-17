package com.example.stripesdemo.domain.entity


data class SettingsDomainEntity(
    val scansDelay: Long,
    val feedbackDelay: Long,
)


fun getDefaultSettings() = SettingsDomainEntity(
    scansDelay = 200L,
    feedbackDelay = 200L,
)