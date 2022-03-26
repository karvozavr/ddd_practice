project.base.archivesName.set("shop-rest")

plugins {
    id(Plugins.spring_kotlin) version PluginVers.spring_kotlin
}

dependencies {
    // project
    implementation(project(":common:types"))
    implementation(project(":common:rest"))
    implementation(project(":shop:domain"))
    implementation(project(":shop:usecase"))

    // kotlin
    implementation(Libs.kotlin_jdk8)
    implementation(Libs.kotlin_reflect)
    implementation(Libs.kotlin_stdlib)
    implementation(Libs.arrow)

    // spring
    implementation(Libs.spring_boot_starter_web)

    // swagger
    implementation(Libs.swagger)

    // jackson
    implementation(Libs.jackson_kotlin)

    // test
    testImplementation(Libs.spring_boot_starter_test) {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }

    testImplementation(Libs.kotest_junit)
    testImplementation(Libs.kotest_arrow)
    testImplementation(Libs.junit_engine)
    testImplementation(Libs.junit_params)

    testImplementation(testFixtures(project(":common:types")))
    testImplementation(testFixtures(project(":common:rest")))
    testImplementation(testFixtures(project(":shop:domain")))
    testImplementation(testFixtures(project(":shop:usecase")))

    testFixturesImplementation(testFixtures(project(":common:types")))
    testFixturesImplementation(testFixtures(project(":common:rest")))
    testFixturesImplementation(testFixtures(project(":shop:domain")))
    testFixturesImplementation(testFixtures(project(":shop:usecase")))
    testFixturesImplementation(Libs.arrow)
    testFixturesImplementation(Libs.kotest_junit)
}