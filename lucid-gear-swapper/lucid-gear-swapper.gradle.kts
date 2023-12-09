version = "2.1.4"

project.extra["PluginName"] = "Lucid Gear Swapper"
project.extra["PluginDescription"] = "Set-up up to 6 custom gear swaps with customizable hotkeys or trigger them via weapon equip"

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