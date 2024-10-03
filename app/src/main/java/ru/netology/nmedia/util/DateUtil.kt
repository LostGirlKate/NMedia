package ru.netology.nmedia.util

import java.time.Duration
import java.time.OffsetDateTime


fun Long.diffWithNowToString(): String {
    val now = OffsetDateTime.now()
    val yesterday = now.minus(Duration.ofDays(1))
    val twoDay = now.minus(Duration.ofDays(2))
    val str = when (this) {
        in yesterday.toEpochSecond() + 1..now.toEpochSecond() -> "Сегодня"
        in twoDay.toEpochSecond() + 1..yesterday.toEpochSecond() -> "Вчера"
        else -> "На прошлой неделе"
    }
    return str
}