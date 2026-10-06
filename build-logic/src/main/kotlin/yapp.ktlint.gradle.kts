import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    id("org.jlleitschuh.gradle.ktlint")
}

if (project.path in setOf(":app", ":core:designsystem")) {
    configure<KtlintExtension> {
        baseline.set(project.file("ktlint-baseline.xml"))
    }
}

configure<KtlintExtension> {
    filter {
        exclude("build/**")
        exclude("**/build/**")
        exclude("**/generated/**")
    }
}
