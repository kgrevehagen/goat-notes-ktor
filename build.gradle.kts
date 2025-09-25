plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aws.dynamodbmapper)
}

group = "com.kgrevehagen"
version = "0.0.1"

application {
    mainClass = "com.kgrevehagen.goatnotes.ApplicationKt"
}

repositories {
    mavenCentral()
    maven { url = uri("https://packages.confluent.io/maven/") }
}

dependencies {
    implementation(platform(awssdk.bom))

    implementation(awssdk.services.dynamodb)
    implementation(libs.aws.dynamodbmapper)
    implementation(libs.aws.dynamodbmapper.annotations)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.kotlin.logging)
    implementation(libs.logback)

    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.test.host)
}
