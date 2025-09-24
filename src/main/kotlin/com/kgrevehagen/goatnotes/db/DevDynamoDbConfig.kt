package com.kgrevehagen.goatnotes.db

import aws.sdk.kotlin.hll.dynamodbmapper.DynamoDbMapper
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.dynamodb.DynamoDbClient
import aws.smithy.kotlin.runtime.ExperimentalApi
import aws.smithy.kotlin.runtime.net.url.Url
import io.ktor.server.config.ApplicationConfig

internal class DevDynamoDbConfig(private val config: ApplicationConfig) : DynamoDbConfig {

    override val dynamoDbClient = DynamoDbClient {
        region = config.property("aws.dynamodb.region").getString()
        endpointUrl = Url.parse(config.property("aws.dynamodb.endpoint").getString())

        credentialsProvider = StaticCredentialsProvider.invoke {
            accessKeyId = config.property("aws.dynamodb.accessKeyId").getString()
            secretAccessKey = config.property("aws.dynamodb.secretAccessKey").getString()
        }
    }

    @OptIn(ExperimentalApi::class)
    override val dynamoDbMapper = DynamoDbMapper(dynamoDbClient)
}