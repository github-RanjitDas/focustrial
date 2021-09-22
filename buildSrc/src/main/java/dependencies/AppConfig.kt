package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "com.cobantch.focusx1"
    const val compileSdkVersion = 29
    const val minSdkVersion = 23
    const val targetSdkVersion = 29
    const val buildVersion = 90
    const val major = 3
    const val minor = 6
    const val patch = 4
    const val versionName = "$major.$minor.$patch"

    val jvmTarget = JavaVersion.VERSION_1_8
}