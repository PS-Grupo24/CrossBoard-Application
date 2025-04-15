plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinxSerialization)
    application
}

group = "org.example.project"
version = "1.0.0"
application {
    mainClass.set("org.example.project.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation("org.jdbi:jdbi3-core:3.32.0")
    implementation("org.jdbi:jdbi3-kotlin:3.28.0")
    implementation("org.jdbi:jdbi3-postgres:3.32.0")
    implementation("org.postgresql:postgresql:42.5.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.bundles.ktorServer)
    //testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    testImplementation("io.ktor:ktor-server-test-host-jvm:3.1.1")
}