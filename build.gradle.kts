import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.4.2"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.4.30"
	kotlin("plugin.spring") version "1.4.30"
	kotlin("plugin.jpa") version "1.4.30"
	kotlin("plugin.serialization") version "1.4.30"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true

group = "no.nsd"
//group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
		exclude("org.apache.tomcat:tomcat-jdbc")
	}
	implementation("org.springframework.data:spring-data-rest-hal-explorer:3.4.1")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.data:spring-data-envers:2.4.3")
	implementation("org.hibernate:hibernate-envers:5.4.25.Final")

	implementation("org.apache.httpcomponents:httpclient")
	implementation("org.apache.httpcomponents:httpclient-cache")


	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0-RC")

	implementation("net.logstash.logback:logstash-logback-encoder:6.5")

	implementation("io.jsonwebtoken:jjwt:0.9.1")
    implementation("com.itextpdf:itext7-core:7.1.14")
    implementation("com.itextpdf:html2pdf:3.0.3")


	runtimeOnly("org.postgresql:postgresql")
//	runtimeOnly("com.h2database:h2")
//	runtimeOnly("org.springframework.boot:spring-boot-devtools")

	// kapt("org.springframework.boot:spring-boot-configuration-processor")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api")
	testImplementation("com.ninja-squad:springmockk:1.1.3")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

}
	

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "1.8"
	}
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
