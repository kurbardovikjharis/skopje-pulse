package com.haris.sensordetails.utils

internal fun averageToString(value: Double?): String {
    if (value == null) return ""
    return String.format("%.2f", value)
}