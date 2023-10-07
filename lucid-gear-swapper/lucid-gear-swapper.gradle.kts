import ProjectVersions.unethicaliteVersion

version = "1.0.0"

project.extra["PluginName"] = "Lucid Gear Swapper"
project.extra["PluginDescription"] = "Set-up up to 6 custom gear swaps with customizable hotkeys"

dependencies {
    annotationProcessor(Libraries.lombok)
    annotationProcessor(Libraries.pf4j)

    compileOnly("net.unethicalite:runelite-api:${unethicaliteVersion}")
    compileOnly("net.unethicalite:runelite-client:${unethicaliteVersion}")

    compileOnly(Libraries.guice)
    compileOnly(Libraries.lombok)
    compileOnly(Libraries.pf4j)
    compileOnly(Libraries.rxjava)
}

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