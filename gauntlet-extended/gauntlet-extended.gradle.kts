version = "6.2.0"

project.extra["PluginName"] = "Gauntlet Extended"
project.extra["PluginDescription"] = "All-in-one plugin for the Gauntlet. Original plugin by xKylee."

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