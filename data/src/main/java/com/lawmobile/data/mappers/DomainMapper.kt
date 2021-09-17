package com.lawmobile.data.mappers

interface DomainMapper<T, Domain> {
    fun T.toDomain(): Domain
}
