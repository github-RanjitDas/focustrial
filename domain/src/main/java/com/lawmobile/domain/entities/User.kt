package com.lawmobile.domain.entities

data class User(
    val id: String? = null,
    val name: String? = null,
    val password: String? = null,
    val devicePassword: String? = null,
    val email: String? = null
)
