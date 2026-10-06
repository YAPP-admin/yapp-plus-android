// Shared build configuration is provided by the build-logic included build.
tasks.register("ktlintCheck") {
    dependsOn(
        subprojects
            .filter { it.buildFile.exists() }
            .map { "${it.path}:ktlintCheck" },
    )
}
