plugins {
    java
}

group = "ovh.npk"
version = "2.0"

repositories {
    mavenCentral()
}

dependencies {
	compileOnly("org.projectlombok:lombok:1.18.22")
	annotationProcessor("org.projectlombok:lombok:1.18.22")
	testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}
