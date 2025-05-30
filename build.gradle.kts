plugins {
	java
	jacoco
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
		// Let Gradle use any available Java 21 implementation
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

val seleniumJavaVersion = "4.14.1"
val seleniumJupiterVersion = "5.0.1"
val webdrivermanagerVersion = "5.6.3"
val junitJupiterVersion = "5.9.1"

dependencies {
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    testImplementation("org.springframework.modulith:spring-modulith-starter-test:1.1.1")

    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation") // <-- ADDED for DTO validation
    implementation("org.springframework.boot:spring-boot-starter-security")  // <-- ADDED for authentication/authorization
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6") // <-- ADDED for Thymeleaf security tags

    testImplementation("org.springframework.modulith:spring-modulith-starter-test:1.1.1")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("org.postgresql:postgresql")
    implementation("me.paulschwarz:spring-dotenv:4.0.0")
    implementation("org.springframework.boot:spring-boot-starter-validation")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.springframework.security:spring-security-test") // <-- ADDED for testing secured endpoints

    testImplementation("org.seleniumhq.selenium:selenium-java:$seleniumJavaVersion")
    testImplementation("io.github.bonigarcia:selenium-jupiter:$seleniumJupiterVersion")
    testImplementation("io.github.bonigarcia:webdrivermanager:$webdrivermanagerVersion")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Monitoring dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // JMeter dependencies for testing
    testImplementation("org.apache.jmeter:ApacheJMeter_core:5.6.3")
    testImplementation("org.apache.jmeter:ApacheJMeter_java:5.6.3")
    testImplementation("org.apache.jmeter:ApacheJMeter_http:5.6.3")
}

tasks.register<Test>("unitTest") {
    description = "Runs unit tests."
    group = "verification"

    filter {
        excludeTestsMatching("*FunctionalTest")
    }
}

tasks.register<Test>("functionalTest") {
    description = "Runs functional tests."
    group = "verification"

    filter {
        includeTestsMatching("*FunctionalTest")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

tasks.test {
    filter {
        excludeTestsMatching("*FunctionalTest")
    }
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true  // Required for SonarQube coverage import
        csv.required = false
        html.outputLocation = layout.buildDirectory.dir("jacocoHtml")
    }
}

sonar {
  properties {
    property("sonar.projectKey", "advprog-a03-gatherlove")
    property("sonar.projectName", "advprog-a03-gatherlove")
    property("sonar.sourceEncoding", "UTF-8")
    property("sonar.java.coveragePlugin", "jacoco")
    property("sonar.coverage.jacoco.xmlReportPaths", "${layout.buildDirectory.asFile.get()}/reports/jacoco/test/jacocoTestReport.xml")
    property("sonar.junit.reportPaths", "${layout.buildDirectory.asFile.get()}/test-results/test")
  }
}
