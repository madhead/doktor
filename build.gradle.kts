plugins {
	kotlin("jvm") version ("1.1.3-2")
}

java {
	sourceCompatibility = JavaVersion.VERSION_1_8
}

task<Wrapper>("wrapper") {
	gradleVersion = "4.1"
	distributionType = Wrapper.DistributionType.ALL
}
