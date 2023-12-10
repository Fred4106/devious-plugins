version = "1.0.4"
project.extra["PluginName"] = "Lucid Cannon Reloader"
project.extra["PluginDescription"] = "A plugin that will reload your cannon so you don't have to"

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