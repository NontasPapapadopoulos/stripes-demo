package com.example.stripesdemo.data.mapper

import com.example.stripesdemo.data.entity.DeviceDataEntity
import com.example.stripesdemo.data.entity.DeviceDetailsDataEntity
import com.example.stripesdemo.domain.entity.DeviceDetailsDomainEntity
import com.example.stripesdemo.domain.entity.DeviceDomainEntity


fun DeviceDomainEntity.toData(): DeviceDataEntity = DeviceDataEntity(
    id = id,
    name = name
)


fun DeviceDataEntity.toDomain(): DeviceDomainEntity = DeviceDomainEntity(
    id = id,
    name = name
)


fun DeviceDetailsDataEntity.toDomain(): DeviceDetailsDomainEntity = DeviceDetailsDomainEntity(
    volume = volume,
    id = id
)
