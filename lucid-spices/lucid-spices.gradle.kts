version = "1.0.4"

project.extra["PluginName"] = "Lucid Spices"
project.extra["PluginDescription"] = "A plugin to help you gather spices for stews and not kill your cat in the process"

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