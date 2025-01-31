plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.trup10ka.xiba"
version = "0.0.1"

/* ====  Utils  ==== */
var jetbrainsAnnotationsVersion = "24.0.0"

repositories {
    mavenCentral()
}

dependencies {
    
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
