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
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.bundles.ktorServer)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.openapi)
    //implementation("io.ktor:ktor-server-core:3.1.1")
    //implementation("io.ktor:ktor-server-swagger:3.1.1")
    //implementation("io.ktor:ktor-server-openapi:3.1.1")
    implementation(libs.ktor.server.sse)
    //testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    //testImplementation("io.ktor:ktor-server-test-host-jvm:3.1.1")
}