package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "com.cobantch.focusx1"
    const val compileSdkVersion = 30
    const val minSdkVersion = 24
    const val targetSdkVersion = 30
    const val buildVersion = 118
    const val major = 3
    const val minor = 7
    const val patch = 6
    const val versionName = "$major.$minor.$patch"

    val jvmTarget = JavaVersion.VERSION_1_8
}