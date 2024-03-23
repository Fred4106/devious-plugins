version = "1.0.0"

plugins {
    scala
}


project.extra["PluginName"] = "Fred Scala Example"
project.extra["PluginDescription"] = "An example plugin in scala that can be copied to use as a plugin skeleton. Does nothing functionally."


val scalaMajorVersion = '3'
val scalaVersion = "$scalaMajorVersion.4.0"

dependencies {
    implementation("org.scala-lang", "scala3-library_" + scalaMajorVersion, "" + scalaVersion)
}
//scala.ext

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