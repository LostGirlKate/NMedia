package ru.netology.nmedia

import kotlin.math.floor


fun Int.toDisplayString(): String {
    return when (this) {
        in 0..<1_000 -> this.toString()
        in 1_000..<1_100 -> (this / 1_000).toString() + "К"
        in 1_100..<10_000 -> (
                if (this % 1_000.0 >= 100)
                    (floor((this / 1_000.0) * 10.0) / 10.0).toString()
                else (this / 1_000).toString()
                ) + "К"

        in 10_000..<1_000_000 -> (this / 1_000).toString() + "К"
        in 1_000_000..<1_100_000 -> (this / 1_000_000).toString() + "М"
        else -> (
                if (this % 1_000_000.0 >= 100)
                    (floor((this / 1_000_000.0) * 10.0) / 10.0).toString()
                else (this / 1_000_000).toString()
                ) + "М"
    }
}