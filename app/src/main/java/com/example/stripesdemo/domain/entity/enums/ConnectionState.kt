package com.example.stripesdemo.domain.entity.enums

enum class ConnectionState(val code: Int) {
    DISCONNECTED(0),
    CONNECTING(1),
    CONNECTED(2),
    DISCONNECTING(3);
}