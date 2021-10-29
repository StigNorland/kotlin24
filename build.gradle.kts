import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.5"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.5.21"
	kotlin("plugin.spring") version "1.5.21"
	kotlin("plugin.jpa") version "1.5.21"
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.useIR = true


group = "no.nsd"
version = "0.9.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11


repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-security:2.5.5")
	implementation("org.springframework.boot:spring-boot-starter-actuator:2.5.5")

	implementation("org.springframework.boot:spring-boot-starter-data-rest:2.5.5")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.5") {
		exclude("org.apache.tomcat:tomcat-jdbc")
	}
	implementation("org.springframework.data:spring-data-rest-hal-explorer:3.5.4")
	implementation("org.springframework.boot:spring-boot-starter-hateoas:2.5.5")
	implementation("org.springframework.data:spring-data-envers:2.5.4")
	implementation("org.hibernate:hibernate-envers:5.4.32.Final")

	implementation("org.apache.httpcomponents:httpclient:4.5.13")
	implementation("org.apache.httpcomponents:httpclient-cache:4.5.13")

	implementation("com.fasterxml.jackson.core:jackson-core:2.13.0")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.13.0")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	implementation("com.google.code.gson:gson:2.8.8")
//	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")

	implementation("net.logstash.logback:logstash-logback-encoder:6.6")

//	implementation("io.jsonwebtoken:jjwt:0.9.1")
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")

	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

    implementation("com.itextpdf:itext7-core:7.1.16")
    implementation("com.itextpdf:html2pdf:3.0.5")



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
