package ru.raider.date.auth

data class User (
    val name: String,
    val email: String,
    var password: String,
    var age: Int
)