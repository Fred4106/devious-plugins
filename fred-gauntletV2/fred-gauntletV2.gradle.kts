import ProjectVersions.unethicaliteVersion
version = "0.0.1"

plugins {
    scala
}

project.extra["PluginName"] = "Fred GauntletV2"
project.extra["PluginDescription"] = "Helps with Gauntlet"

val scalaMajorVersion = '3'
val scalaVersion = "$scalaMajorVersion.4.0"

dependencies {
    implementation("org.scala-lang", "scala3-library_" + scalaMajorVersion, "" + scalaVersion)
}

tasks {
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