package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "com.cobantch.focusx1"
    const val compileSdkVersion = 30
    const val minSdkVersion = 24
    const val targetSdkVersion = 30
    const val buildVersion = 124
    private const val major = 3
    private const val minor = 8
    private const val patch = 0
    const val versionName = "$major.$minor.$patch"

    val jvmTarget = JavaVersion.VERSION_1_8
}
