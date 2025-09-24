package com.kgrevehagen.goatnotes.model

import kotlinx.serialization.Serializable

@Serializable
data class PageInfo<T>(val items: List<T>, val lastEvaluatedKey: String?)
