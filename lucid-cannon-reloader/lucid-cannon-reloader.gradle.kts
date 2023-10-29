import ProjectVersions.unethicaliteVersion

version = "0.0.1"
project.extra["PluginName"] = "Lucid Cannon Reloader"
project.extra["PluginDescription"] = "A plugin that will reload your cannon so you don't have to"

plugins{
    kotlin("kapt")
}

dependencies {
    annotationProcessor(Libraries.lombok)
    annotationProcessor(Libraries.pf4j)

    kapt(Libraries.pf4j)
    compileOnly("net.unethicalite:runelite-api:${unethicaliteVersion}")
    compileOnly("net.unethicalite:runelite-client:${unethicaliteVersion}")

    compileOnly(Libraries.guice)
    compileOnly(Libraries.lombok)
    compileOnly(Libraries.pf4j)
    compileOnly(Libraries.rxjava)
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "11"
            freeCompilerArgs = listOf("-Xjvm-default=all-compatibility")
        }
        sourceCompatibility = "11"
    }
    jar {
        manifest {
            attributes(mapOf(
                "Plugin-Version" to project.version,
                "Plugin-Id" to nameToId(project.extra["PluginName"] as String),
                "Plugin-Provider" to project.extra["PluginProvider"],
                "Plugin-Description" to project.extra["PluginDescription"],
                "Plugin-License" to project.extra["PluginLicense"]
            ))
        }
    }
}