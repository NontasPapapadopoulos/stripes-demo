package com.example.stripesdemo.data.mapper

import com.example.stripesdemo.data.entity.SettingsDataEntity
import com.example.stripesdemo.domain.entity.SettingsDomainEntity


fun SettingsDataEntity.toDomain() = SettingsDomainEntity(
    scansDelay = scansDelay,
    feedbackDelay = feedbackDelay,
    connectionUUID = connectionUUID
)


fun SettingsDomainEntity.toData() = SettingsDataEntity(
    scansDelay = scansDelay,
    feedbackDelay = feedbackDelay,
    connectionUUID = connectionUUID
)