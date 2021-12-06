package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "com.cobantch.focusx1"
    const val compileSdkVersion = 30
    const val minSdkVersion = 23
    const val targetSdkVersion = 30
    const val buildVersion = 97
    const val major = 3
    const val minor = 7
    const val patch = 0
    const val versionName = "$major.$minor.$patch"

    val jvmTarget = JavaVersion.VERSION_1_8
}