package dependencies

object Build {
    const val STRING_TYPE = "String"
    const val STRING_ARRAY_TYPE = "String[]"

    const val VALIDATE_USER_URL_FIELD = "VALIDATE_USER_URL"
    const val VALIDATE_USER_URL_DEBUG_VALUE = "\"validateOfficerId/debug\""
    const val VALIDATE_USER_URL_RELEASE_VALUE = "\"validateOfficerId/release\""
    const val VALIDATE_USER_URL_STAGING_VALUE = "\"validateOfficerId/staging\""
    const val VALIDATE_USER_URL_QATEST_VALUE = "\"validateOfficerId/qatest\""

    // pending to define client secret for other build variants different to debug
    const val SSO_CLIENT_SECRET_FIELD = "SSO_CLIENT_SECRET"
    const val SSO_CLIENT_SECRET_DEBUG_VALUE = "\"7d2c074d-855a-f5d6-b697-44d2bc602754\""
    const val SSO_CLIENT_SECRET_RELEASE_VALUE = "\"d7f1b234-9812-4c56-8ef0-3d9aebc40abc\""
    const val SSO_CLIENT_SECRET_STAGING_VALUE = "\"c76f923a-541d-48e2-80ab-ec03d7ba95b2\""
    const val SSO_CLIENT_SECRET_QATEST_VALUE = "\"e1a88c57-6d8f-49be-a37d-2e97ab32156a\""

    const val SSO_CLIENT_ID_FIELD = "SSO_CLIENT_ID"
    const val SSO_CLIENT_ID_VALUE = "\"focusmobileapp\""

    const val SSO_CALLBACK_FIELD = "SSO_CALLBACK"
    const val SSO_CALLBACK_VALUE = "\"focus-mobile://callback\""

    const val SSO_AUTH_SCOPES_FIELD = "SSO_AUTH_SCOPES"
    const val SSO_AUTH_SCOPES_VALUE = "{" +
            "\"commandcenterapi\"," +
            "\"openid\"," +
            "\"email\"," +
            "\"profile\"," +
            "\"hardware-read\"," +
            "\"tenant-security-api\"," +
            "\"tenant-security-api-write-users\"," +
            "\"internal-security-read-users\"" +
            "}"

    const val DISCOVERY_URL_FIELD = "DISCOVERY_USER_URL"
    const val DISCOVERY_URL_DEBUG_VALUE = "\"https://dev.safefleetcloud.us:443/tenant-settings/api/hardware/discovery\""
    const val DISCOVERY_URL_RELEASE_VALUE = "\"https://safefleetcloud.us:443/tenant-settings/api/hardware/discovery\""
    const val DISCOVERY_URL_STAGING_VALUE = "\"https://stage.safefleetcloud.us:443/tenant-settings/api/hardware/discovery\""
    const val DISCOVERY_URL_QATEST_VALUE = "\"https://qatest.safefleetcloud.us:443/tenant-settings/api/hardware/discovery\""
}