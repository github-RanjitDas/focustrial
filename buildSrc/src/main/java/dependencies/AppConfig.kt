package dependencies

import org.gradle.api.JavaVersion

object AppConfig {
    const val applicationId = "net.safefleet.focus"
    const val compileSdkVersion = 33
    const val minSdkVersion = 24
    const val targetSdkVersion = 33
    //buildVersion will be different for release
    const val buildVersion = 1
    private const val major = 3
    private const val minor = 8
    private const val patch = 0
    //versionName will be different for release
    const val versionName = "$major.$minor.$patch"
    val jvmTarget = JavaVersion.VERSION_1_8
}
