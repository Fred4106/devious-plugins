version = "1.0.1"

project.extra["PluginName"] = "Lucid Example"
project.extra["PluginDescription"] = "An example plugin that can be copied to use as a plugin skeleton. Does nothing functionally."

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