import org.jenkinsci.gradle.plugins.jpi.JpiDeveloper
import org.jenkinsci.gradle.plugins.jpi.JpiLicense
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version ("1.2.21")
	kotlin("kapt") version ("1.2.21")

	id("jacoco")
	id("org.jenkins-ci.jpi") version ("0.23.1")
	id("net.researchgate.release") version ("2.6.0")
}

repositories {
	jcenter()
}

val kotlinVersion by project
val rxkotlinVersion by project
val flexmarkVersion by project
val asciidoctorjVersion by project
val asciidoctorjDiagramVersion by project
val jacksonVersion by project
val jgraphtVersion by project
val fuelVersion by project
val jsoupVersion by project
val kotlinxHtmlJvmVersion by project
val simplemagicVersion by project
val commonsCodecVersion by project
val jenkinsCredentialsPluginVersion by project
val jenkinsWorkflowStepsAPIPluginVersion by project

val testngVersion by project
val wiremockVersion by project
val mockitoVersion by project

val sezpozVersion by project

dependencies {
	compile(kotlin("stdlib-jre8", "${kotlinVersion}"))
	compile(kotlin("reflect", "${kotlinVersion}"))
	compile("io.reactivex.rxjava2:rxkotlin:${rxkotlinVersion}")
	compile("com.vladsch.flexmark:flexmark:${flexmarkVersion}")
	compile("com.vladsch.flexmark:flexmark-ext-yaml-front-matter:${flexmarkVersion}")
	compile("com.vladsch.flexmark:flexmark-ext-tables:${flexmarkVersion}")
	compile("org.asciidoctor:asciidoctorj:${asciidoctorjVersion}")
	compile("org.asciidoctor:asciidoctorj-diagram:${asciidoctorjDiagramVersion}")
	compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${jacksonVersion}")
	compile("com.fasterxml.jackson.module:jackson-module-kotlin:${jacksonVersion}")
	compile("org.jgrapht:jgrapht-core:${jgraphtVersion}")
	compile("com.github.kittinunf.fuel:fuel-rxjava:${fuelVersion}")
	compile("com.github.kittinunf.fuel:fuel-gson:${fuelVersion}")
	compile("org.jsoup:jsoup:${jsoupVersion}")
	compile("org.jetbrains.kotlinx:kotlinx-html-jvm:${kotlinxHtmlJvmVersion}")
	compile("com.j256.simplemagic:simplemagic:${simplemagicVersion}")
	compile("commons-codec:commons-codec:${commonsCodecVersion}")

	jenkinsPlugins("org.jenkins-ci.plugins:credentials:${jenkinsCredentialsPluginVersion}")
	jenkinsPlugins("org.jenkins-ci.plugins.workflow:workflow-step-api:${jenkinsWorkflowStepsAPIPluginVersion}")

	testCompile("org.testng:testng:${testngVersion}")
	testCompile("com.github.tomakehurst:wiremock:${wiremockVersion}")
	testCompile("org.mockito:mockito-core:${mockitoVersion}")

	// SezPoz is used to process @hudson.Extension and other annotations
	kapt("net.java.sezpoz:sezpoz:${sezpozVersion}")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

kapt {
	correctErrorTypes = true
}

jacoco {
	toolVersion = "0.7.9"
}

val jenkinsCoreVersion by project

jenkinsPlugin {
	displayName = "Doktor"
	shortName = "doktor"
	gitHubUrl = "https://github.com/madhead/doktor"
	url = "https://wiki.jenkins.io/display/JENKINS/Doktor+Plugin"

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

tasks.withType(JacocoReport::class.java).all {
	reports {
		xml.setEnabled(true)
	}
}

tasks.withType(KaptTask::class.java).all {
	outputs.upToDateWhen { false }
}

tasks.withType(KaptGenerateStubsTask::class.java).all {
	outputs.upToDateWhen { false }
}

task<Wrapper>("wrapper") {
	gradleVersion = "4.5.1"
	distributionType = Wrapper.DistributionType.ALL
}
