plugins {
    id("org.jetbrains.intellij") version "0.6.5"
    java
    kotlin("jvm") version "1.4.10"
}

group = "com.magicbytes"
version = "1.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "201.8743.12"
    type = "IC"
    updateSinceUntilBuild = false
    setPlugins("org.jetbrains.kotlin", "android")

    alternativeIdePath = "/Users/alex/Library/Application Support/JetBrains/Toolbox/apps/AndroidStudio/ch-0/201.6953283/Android Studio.app/Contents"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.getByName<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")
}