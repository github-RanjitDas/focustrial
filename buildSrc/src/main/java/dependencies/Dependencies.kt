package dependencies

object Dependencies {
    object AndroidX {
        private const val VERSION = "1.3.2"
        private const val TEST_VERSION = "1.3.0"
        const val coreKTX = "androidx.core:core-ktx:$VERSION"
        const val testRunner = "androidx.test:runner:$TEST_VERSION"
        const val testRules = "androidx.test:rules:$TEST_VERSION"
        const val testExtJunit = "androidx.test.ext:junit:1.1.2"
        const val testOrchestrator = "androidx.test:orchestrator:$TEST_VERSION"
    }

    object AppCompat {
        private const val VERSION = "1.2.0"
        const val appCompat = "androidx.appcompat:appcompat:$VERSION"
    }

    object Auth0 {
        private const val VERSION = "2.0.0"
        const val jwtDecode = "com.auth0.android:jwtdecode:$VERSION"
    }

    object Barista {
        private const val VERSION = "3.6.0"
        const val barista = "com.schibsted.spain:barista:$VERSION"
    }

    object Base {
        const val androidCommons = "com.safefleet.mobile:android-commons:2.4.1-SNAPSHOT@aar"
        const val externalHardware = "com.safefleet.mobile:external-hardware:5.13.9-SNAPSHOT@jar"
        const val kotlinCommons = "com.safefleet.mobile:kotlin-commons:2.4.0-SNAPSHOT@jar"
        const val safeFleetUI = "com.safefleet.mobile:safefleet-ui:1.4.3@aar"
        const val authentication = "com.safefleet.mobile:authentication:2.5.5-SNAPSHOT@aar"
    }

    object ConstraintLayout {
        private const val VERSION = "2.0.4"
        const val constraintLayout = "androidx.constraintlayout:constraintlayout:$VERSION"
    }

    object Coroutines {
        private const val VERSION = "1.5.2"
        const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$VERSION"
        const val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$VERSION"
        const val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$VERSION"
    }

    object DataStore {
        const val preferences = "androidx.datastore:datastore-preferences:1.0.0"
    }

    object Espresso {
        private const val VERSION = "3.3.0"
        const val espressoCore = "androidx.test.espresso:espresso-core:$VERSION"
        const val idlingResource = "androidx.test.espresso:espresso-idling-resource:$VERSION"
        const val intents = "androidx.test.espresso:espresso-intents:$VERSION"
    }

    object Fragment {
        private const val VERSION = "1.2.5"
        const val extensions = "androidx.fragment:fragment-ktx:$VERSION"
    }

    object Glide {
        private const val VERSION = "4.11.0"
        const val glide = "com.github.bumptech.glide:glide:$VERSION"
    }

    object Google {
        const val gson = "com.google.code.gson:gson:2.8.6"
        const val findBugs = "com.google.code.findbugs:jsr305:3.0.2"
        const val flexBox = "com.google.android.flexbox:flexbox:3.0.0"
    }

    object Hilt {
        private const val VERSION = "2.33-beta"
        private const val JETPACK_VERSION = "1.0.0-alpha03"
        const val hiltAndroid = "com.google.dagger:hilt-android:$VERSION"
        const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:$VERSION"
        const val hiltCompiler = "androidx.hilt:hilt-compiler:$JETPACK_VERSION"
        const val hiltViewModel = "androidx.hilt:hilt-lifecycle-viewmodel:$JETPACK_VERSION"
        const val gradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$VERSION"
    }

    object JUnit {
        private const val VERSION = "4.13.1"
        const val junit = "junit:junit:$VERSION"
    }

    object JUnit5 {
        private const val VERSION = "5.5.1"
        const val jupiterApi = "org.junit.jupiter:junit-jupiter-api:$VERSION"
        const val jupiterEngine = "org.junit.jupiter:junit-jupiter-engine:$VERSION"
        const val jupiterParams = "org.junit.jupiter:junit-jupiter-params:$VERSION"
    }

    object Kotlin {
        private const val VERSION = "1.4.32"
        const val standardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$VERSION"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    }

    object Ktor {
        private const val VERSION = "1.6.3"
        const val clientCore = "io.ktor:ktor-client-core:$VERSION"
        const val clientAndroid = "io.ktor:ktor-client-android:$VERSION"
        const val clientSerialization = "io.ktor:ktor-client-serialization:$VERSION"
        const val clientMock = "io.ktor:ktor-client-mock:$VERSION"
    }

    object Lifecycle {
        private const val VERSION = "2.4.0-alpha03"
        private const val EXT_VERSION = "2.2.0"
        const val extensions = "androidx.lifecycle:lifecycle-extensions:$EXT_VERSION"
        const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:$VERSION"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    }

    object Lottie {
        private const val VERSION = "3.4.0"
        const val lottie = "com.airbnb.android:lottie:$VERSION"
    }

    object Material {
        private const val VERSION = "1.3.0"
        const val material = "com.google.android.material:material:$VERSION"
    }

    object Mockk {
        private const val VERSION = "1.9"
        const val mockk = "io.mockk:mockk:$VERSION"
        const val mockkAndroid = "io.mockk:mockk-android:$VERSION"
    }

    object OpenId {
        private const val VERSION = "0.7.1"
        const val appAuth = "net.openid:appauth:$VERSION"
    }

    object NewRelic {
        const val android = "com.newrelic.agent.android:android-agent:6.3.1"
    }

    object PDF {
        private const val VERSION = "3.2.0-beta.1"
        const val pdfViewer = "com.github.barteksc:android-pdf-viewer:$VERSION"
    }

    object RecyclerView {
        private const val VERSION = "1.1.0"
        const val recyclerView = "androidx.recyclerview:recyclerview:$VERSION"
    }

    object SqlDelight {
        private const val VERSION = "1.4.4"
        const val androidDriver = "com.squareup.sqldelight:android-driver:$VERSION"
        const val sqlDelightDriver = "com.squareup.sqldelight:sqlite-driver:$VERSION"
        const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    }

    object UIAutomator {
        private const val VERSION = "2.2.0"
        const val uiAutomator = "androidx.test.uiautomator:uiautomator:$VERSION"
    }

    object VLC {
        private const val VERSION = "3.4.3"
        const val libVLC = "org.videolan.android:libvlc-all:$VERSION"
    }
}