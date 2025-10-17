package com.example.stripesdemo.domain.entity.enums

enum class ScanField(val dbField: String, val defaultIfEmpty: String ) {
    Barcode("Barcode", ""),
    Count("Count",  "0");
}

