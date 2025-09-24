package com.kgrevehagen.goatnotes.db.util

import aws.sdk.kotlin.services.dynamodb.model.AttributeValue
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.double
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.long
import kotlinx.serialization.json.longOrNull

/**
 * A utility class for mapping between Kotlin data classes and DynamoDB items using JSON serialization.
 */
internal class DynamoItemMapper {

    inline fun <reified T> toDynamoItem(value: T): Map<String, AttributeValue> {
        val json = Json.encodeToJsonElement(value).jsonObject
        return jsonToDynamoItem(json)
    }

    fun toDynamoItem(value: String): Map<String, AttributeValue> {
        val jsonElement = Json.parseToJsonElement(value).jsonObject
        return jsonToDynamoItem(jsonElement)
    }

    inline fun <reified T> toItem(item: Map<String, AttributeValue>): T {
        val s = toJson(item)
        return Json.decodeFromString(s)
    }

    fun toJson(item: Map<String, AttributeValue>): String {
        val jsonMap = item.mapValues { (_, value) ->
            when {
                value.asSOrNull() != null -> JsonPrimitive(value.asS())
                value.asNOrNull() != null -> JsonPrimitive(value.asN().toLong())
                else -> JsonNull
            }
        }
        return Json.encodeToString(JsonObject(jsonMap))
    }

    private fun jsonToDynamoItem(json: JsonObject): Map<String, AttributeValue> {
        return json.mapValues { (_, value) ->
            when (value) {
                is JsonPrimitive -> {
                    when {
                        value.isString -> AttributeValue.S(value.content)
                        value.booleanOrNull != null -> AttributeValue.Bool(value.boolean)
                        value.longOrNull != null -> AttributeValue.N(value.long.toString())
                        value.doubleOrNull != null -> AttributeValue.N(value.double.toString())
                        else -> AttributeValue.Null(true)
                    }
                }

                JsonNull -> AttributeValue.Null(true)
                else -> throw IllegalArgumentException("Unsupported JSON structure: $value")
            }
        }
    }
}