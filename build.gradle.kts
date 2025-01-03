import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.retheviper"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(21)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

tasks {
    register<Copy>("bundleBinaries") {
        val from = "src/jvmMain/resources/binaries"

        val ffmpegSource = "$from/ffmpeg"
        val target = layout.projectDirectory.dir(from)

        from(ffmpegSource)
        into(target)

        val ytDlpSource = "$path/yt-dlp"

        from(ytDlpSource)
        into(target)

        onlyIf {
            gradle.startParameter.taskNames.none { it == "run" }
        }
    }

    named("processResources") {
        dependsOn("bundleBinaries")
    }
}

compose.desktop {
    application {
        mainClass = "com.retheviper.youtube_downloader.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg)
            packageName = "youtube-downloader"
            packageVersion = "1.0.0"
        }
    }
}
