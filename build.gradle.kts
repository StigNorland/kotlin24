import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.5.8"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.10"
	kotlin("plugin.spring") version "1.6.10"
	kotlin("plugin.jpa") version "1.6.10"
}


group = "no.nsd"
version = "0.9.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11


repositories {
	mavenCentral()
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-rest")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	{
		exclude("org.apache.tomcat:tomcat-jdbc")
	}
	implementation("org.springframework.boot:spring-boot-starter-jetty")

//	implementation("org.springframework.data:spring-data-rest-hal-browser")
	implementation("org.springframework.data:spring-data-rest-hal-explorer")
	implementation("org.springframework.boot:spring-boot-starter-hateoas")
	implementation("org.springframework.data:spring-data-envers")

	implementation("org.apache.httpcomponents:httpclient:4.5.13")
	implementation("org.apache.httpcomponents:httpclient-cache:4.5.13")
	implementation("org.apache.commons:commons-lang3:3.12.0")
	implementation("io.jsonwebtoken:jjwt-api:0.11.2")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.1")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.2")

	implementation("com.google.code.gson:gson:2.9.0")
	implementation("net.logstash.logback:logstash-logback-encoder:7.0.1")

    implementation("com.itextpdf:itext7-core:7.2.1")
    implementation("com.itextpdf:html2pdf:4.0.1")
	implementation("org.postgresql:postgresql")

	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.2")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.2")

	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
		exclude(module = "mockito-core")
	}
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
	testImplementation("com.ninja-squad:springmockk:3.1.1")

	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

allOpen {
	annotation("javax.persistence.Entity")
	annotation("javax.persistence.Embeddable")
	annotation("javax.persistence.MappedSuperclass")
}


