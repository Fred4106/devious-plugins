version = "1.2.3"

project.extra["PluginName"] = "Lucid Hotkeys"
project.extra["PluginDescription"] = "Setup hotkeys that can do a variety of different actions."

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