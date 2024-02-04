version = "1.0.0"

project.extra["PluginName"] = "Lucid Combat"
project.extra["PluginDescription"] = "Can do auto-combat plus can upkeep HP, prayer, boosts and auto-spec, all are toggle-able."

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