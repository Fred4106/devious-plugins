version = "1.0.2"

project.extra["PluginName"] = "Lucid Discord Logger"
project.extra["PluginDescription"] = "A plugin that sends various messages to a specified Discord webhook URL"

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