version = "1.0.1"

project.extra["PluginName"] = "Lucid Disable Rendering"
project.extra["PluginDescription"] = "Disabled rendering graphics on the client while plugin is enabled."

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