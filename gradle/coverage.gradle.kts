import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoCoverageVerification
import org.gradle.testing.jacoco.tasks.JacocoReport

apply(plugin = "jacoco")

tasks.withType<Test>().configureEach {
    extensions.configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

val coverageExcludes = listOf(
    "**/R.class",
    "**/R$*.class",
    "**/BuildConfig.*",
    "**/Manifest*.*",
    "**/ComposableSingletons*.class",
    "**/*_Impl.class",
    "**/*_Impl$*.class",
    "**/*_Factory.class",
    "**/*_Factory$*.class",
    "**/*_MembersInjector.class",
    "**/Hilt_*.class",
    "**/*_HiltModules*.class",
    "**/*_GeneratedInjector.class",
    "**/*_ComponentTreeDeps*.class",
    "**/*Module_Provide*Factory*.class",
    "**/Dagger*.class",
    "**/*_HiltComponents*.class",
    "**/hilt_aggregated_deps/**",
    "**/dagger/hilt/**",
)

val coverageClasses = (
    if (project.name == "app") {
        files(
            layout.buildDirectory.dir(
                "intermediates/classes/debug/transformDebugClassesWithAsm/dirs",
            ),
        )
    } else {
        files(
            layout.buildDirectory.dir(
                "intermediates/built_in_kotlinc/debug/compileDebugKotlin/classes",
            ),
            layout.buildDirectory.dir(
                "intermediates/javac/debug/compileDebugJavaWithJavac/classes",
            ),
        )
    }
    ).asFileTree.matching {
    exclude(coverageExcludes)
}

val coverageData = layout.buildDirectory.asFileTree.matching {
    include("outputs/unit_test_code_coverage/**/*.exec")
}

val coverageReport = tasks.register<JacocoReport>("coverageReport") {
    group = "verification"
    description = "Laporan cakupan unit test, termasuk Compose UI test lewat Robolectric (debug)."

    dependsOn("testDebugUnitTest")

    executionData.setFrom(coverageData)
    classDirectories.setFrom(coverageClasses)
    sourceDirectories.setFrom(files("src/main/java"))

    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
}

tasks.register<JacocoCoverageVerification>("coverageCheck") {
    group = "verification"
    description = "Gagal bila cakupan baris di bawah 80%."

    dependsOn(coverageReport)

    executionData.setFrom(coverageData)
    classDirectories.setFrom(coverageClasses)
    sourceDirectories.setFrom(files("src/main/java"))

    violationRules {
        rule {
            limit {
                counter = "LINE"
                value = "COVEREDRATIO"
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}
