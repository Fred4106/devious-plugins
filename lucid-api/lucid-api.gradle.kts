version = "0.0.1"

project.extra["PluginName"] = "Lucid API"
project.extra["PluginDescription"] = "API Plugin for all Lucid Plugins. Used as a wrapper to support multiple client APIs"

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