plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.trup10ka.xiba"
version = "0.0.1"

/* ==== Config ==== */
var hoconParserVersion = "3.8.1"

/* ==== Logging ==== */
var slf4j = "2.0.16"
var logback = "1.5.15"

/* ====  Utils  ==== */
var jetbrainsAnnotationsVersion = "24.0.0"

repositories {
    mavenCentral()
}

dependencies {
    
    /* ==== Config ==== */
    implementation("com.electronwill.night-config:hocon:$hoconParserVersion")
    
    /* ==== Logging ==== */
    implementation("org.slf4j:slf4j-api:$slf4j")
    implementation("ch.qos.logback:logback-classic:$logback")
    
    /* ====  Utils  ==== */
    implementation("org.jetbrains:annotations:$jetbrainsAnnotationsVersion")
    
    /* ==== JUnit 5 ==== */
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.trup10ka.kappa.Main")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(22))
    }
}

tasks.test {
    useJUnitPlatform()
}
