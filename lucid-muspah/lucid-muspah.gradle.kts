version = "1.0.3"

project.extra["PluginName"] = "Lucid Muspah"
project.extra["PluginDescription"] = "Helper plugin for the Phantom Muspah (still in development)"

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