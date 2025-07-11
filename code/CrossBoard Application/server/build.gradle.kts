plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinxSerialization)
    application
}

version = "1.0.0"
application {
    mainClass.set("com.crossBoard.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(libs.jdbi3.core)
    implementation(libs.jdbi3.kotlin)
    implementation(libs.jdbi3.postgres)
    implementation(libs.postgresql)
    //noinspection UseTomlInstead
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.bundles.ktorServer)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.sse)
    implementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.jetbrains.kotlin.test)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.junit.jupiter)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}