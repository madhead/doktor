import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version ("1.1.3-2")
	kotlin("kapt") version ("1.1.3-2")

	id("org.jenkins-ci.jpi") version ("0.22.0")
}

repositories {
	jcenter()
}

val kotlinVersion by project
val sezpozVersion by project
val jenkinsCredentialsPluginVersion by project
val jenkinsWorkflowStepsAPIPluginVersion by project

dependencies {
	compile(kotlin("stdlib-jre8", "${kotlinVersion}"))

	// SezPoz is used to process @hudson.Extension and other annotations
	kapt("net.java.sezpoz:sezpoz:${sezpozVersion}")

	jenkinsPlugins("org.jenkins-ci.plugins:credentials:${jenkinsCredentialsPluginVersion}@jar")
	jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-step-api:${jenkinsWorkflowStepsAPIPluginVersion}@jar")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

kapt {
	correctErrorTypes = true
}

tasks.withType(KotlinCompile::class.java).all {
	dependsOn("localizer")

	kotlinOptions {
		jvmTarget = "1.8"
	}
}

tasks.withType(KaptTask::class.java).all {
	outputs.upToDateWhen { false }
}

tasks.withType(KaptGenerateStubsTask::class.java).all {
	outputs.upToDateWhen { false }
}

val jenkinsCoreVersion by project

jenkinsPlugin {
	displayName = "Doktor"
	shortName = "doktor"
	gitHubUrl = "https://github.com/madhead/doktor"

	coreVersion = jenkinsCoreVersion as String?
	compatibleSinceVersion = coreVersion
	fileExtension = "jpi"
	pluginFirstClassLoader = true

	developers = this.Developers().apply {
		developer(delegateClosureOf<JpiDeveloper> {
			setProperty("id", "madhead")
			setProperty("name", "Siarhei Krukau")
			setProperty("email", "siarhei.krukau@gmail.com")
			setProperty("url", "https://madhead.me")
			setProperty("timezone", "UTC+3")
		})
	}

	licenses = this.Licenses().apply {
		license(delegateClosureOf<JpiLicense> {
			setProperty("url", "http://www.apache.org/licenses/LICENSE-2.0")
		})
	}
}

task<Wrapper>("wrapper") {
	gradleVersion = "4.1"
	distributionType = Wrapper.DistributionType.ALL
}
