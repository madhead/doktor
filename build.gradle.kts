import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version ("1.1.4")
	kotlin("kapt") version ("1.1.4")

	id("org.jenkins-ci.jpi") version ("0.22.0")
}

repositories {
	jcenter()
}

val kotlinVersion by project
val rxkotlinVersion by project
val flexmarkVersion by project
val asciidoctorVersion by project
val jgraphtVersion by project
val fuelVersion by project
val jenkinsCredentialsPluginVersion by project
val jenkinsWorkflowStepsAPIPluginVersion by project

val testngVersion by project
val jacksonVersion by project
val wiremockVersion by project

val sezpozVersion by project

dependencies {
	compile(kotlin("stdlib-jre8", "${kotlinVersion}"))
	compile("io.reactivex.rxjava2:rxkotlin:${rxkotlinVersion}")
	compile("com.vladsch.flexmark:flexmark:${flexmarkVersion}")
	compile("com.vladsch.flexmark:flexmark-ext-yaml-front-matter:${flexmarkVersion}")
	compile("org.asciidoctor:asciidoctorj:${asciidoctorVersion}")
	compile("org.jgrapht:jgrapht-core:${jgraphtVersion}")
	compile("com.github.kittinunf.fuel:fuel-rxjava:${fuelVersion}")
	compile("com.github.kittinunf.fuel:fuel-gson:${fuelVersion}")

	jenkinsPlugins("org.jenkins-ci.plugins:credentials:${jenkinsCredentialsPluginVersion}@jar")
	jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-step-api:${jenkinsWorkflowStepsAPIPluginVersion}@jar")

	testCompile("org.testng:testng:${testngVersion}")
	testCompile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${jacksonVersion}")
	testCompile("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
	testCompile("com.github.tomakehurst:wiremock:${wiremockVersion}")

	// SezPoz is used to process @hudson.Extension and other annotations
	kapt("net.java.sezpoz:sezpoz:${sezpozVersion}")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

kapt {
	correctErrorTypes = true
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

tasks.withType(KotlinCompile::class.java).all {
	dependsOn("localizer")

	kotlinOptions {
		jvmTarget = "1.8"
	}
}

tasks.withType(Test::class.java).all {
	useTestNG()
}

tasks.withType(KaptTask::class.java).all {
	outputs.upToDateWhen { false }
}

tasks.withType(KaptGenerateStubsTask::class.java).all {
	outputs.upToDateWhen { false }
}

task<Wrapper>("wrapper") {
	gradleVersion = "4.1"
	distributionType = Wrapper.DistributionType.ALL
}
