package com.lawmobile.presentation.ui.login.x2

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.AuthorizationEndpoints
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.BackOfficeType
import com.lawmobile.domain.usecase.LoginUseCases
import com.lawmobile.domain.utils.PreferencesManager
import com.lawmobile.presentation.BuildConfig
import com.lawmobile.presentation.authentication.AuthStateManagerFactory
import com.lawmobile.presentation.bluetooth.FetchConfigBleManager
import com.lawmobile.presentation.bluetooth.OnBleStatusUpdates
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.keystore.KeystoreHandler
import com.lawmobile.presentation.ui.login.LoginBaseViewModel
import com.lawmobile.presentation.ui.login.state.LoginState
import com.lawmobile.presentation.utils.FeatureSupportHelper
import com.safefleet.mobile.authentication.AuthStateManager
import com.safefleet.mobile.kotlin_commons.helpers.Result
import com.safefleet.mobile.kotlin_commons.helpers.network_manager.ListenableNetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.TokenResponse
import org.json.JSONObject
import javax.inject.Inject
import kotlin.reflect.KFunction1

@HiltViewModel
class LoginX2ViewModel @Inject constructor(
    private val loginUseCases: LoginUseCases,
    private val authStateManagerFactory: AuthStateManagerFactory,
    private val preferencesManager: PreferencesManager,
    private val ioDispatcher: CoroutineDispatcher,
    private val bleManager: FetchConfigBleManager,
    wifiHelper: WifiHelper,
    private val bluetoothAdapter: BluetoothAdapter,
    private val simpleNetworkManager: ListenableNetworkManager,
) : LoginBaseViewModel(loginUseCases.getUserFromCamera, wifiHelper, ioDispatcher) {

    var officerId: String = ""
    var retryCounter = 1

    override val mutableLoginState by lazy { MutableStateFlow<LoginState>(LoginState.X2.OfficerId) }

    private lateinit var authStateManager: AuthStateManager

    val authEndpointsResult: LiveData<Result<AuthorizationEndpoints>> get() = _authEndpointsResult
    private val _authEndpointsResult by lazy { MediatorLiveData<Result<AuthorizationEndpoints>>() }

    val authRequestResult: LiveData<Result<AuthorizationRequest>> get() = _authRequestResult
    private val _authRequestResult by lazy { MediatorLiveData<Result<AuthorizationRequest>>() }

    val devicePasswordResult: LiveData<Result<String>> get() = _devicePasswordResult
    private val _devicePasswordResult by lazy { MediatorLiveData<Result<String>>() }

    val updateConfigProgress: LiveData<Result<String>> get() = _updateConfigProgress
    private val _updateConfigProgress by lazy { MediatorLiveData<Result<String>>() }

    fun getAuthorizationEndpoints() {
        Log.d(TAG, "Start Fetching EndPoints from Internet for SSO Login.")
        viewModelScope.launch(ioDispatcher) {
            _authEndpointsResult.postValue(loginUseCases.getAuthorizationEndpoints())
        }
    }

    fun getDevicePassword(uuid: String) {
        viewModelScope.launch(ioDispatcher) {
            _devicePasswordResult.postValue(loginUseCases.getDevicePassword(uuid))
        }
    }

    fun isUserAuthorized(): Boolean {
        return if (this::authStateManager.isInitialized) {
            authStateManager.isCurrentAuthStateAuthorized()
        } else {
            false
        }
    }

    fun authenticateToGetToken(
        response: AuthorizationResponse,
        callback: (Result<TokenResponse>) -> Unit
    ) {
        authStateManager.exchangeAuthorizationCode(
            response, BuildConfig.SSO_CLIENT_SECRET, callback
        )
    }

    fun getAuthorizationRequest(authorizationEndpoints: AuthorizationEndpoints) {
        Log.d(TAG, "Send Authorization Request for SSO Login.")
        if (!this::authStateManager.isInitialized) {
            authStateManager = authStateManagerFactory.create(authorizationEndpoints)
        }

        viewModelScope.launch(ioDispatcher) {
            _authRequestResult.postValue(authStateManager.createRequestToAuth())
        }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(ioDispatcher) {
            preferencesManager.saveToken(token)
        }
    }

    internal fun fetchConfigFromBle(context: Context) {
        retryCounter = 0
        bleManager.initManager(context)
        scanNConnectFromBluetooth(context, ::retryFetchConfig)
    }

    private fun retryFetchConfig(context: Context) {
        if (MAX_RETRY_ATTEMPT > retryCounter) {
            retryCounter++
            Log.d("Retry", "Retry BLE Scan: attempt:$retryCounter")
            scanNConnectFromBluetooth(context, ::retryFetchConfig)
        } else {
            _updateConfigProgress.value =
                Result.Error(Exception("Unable to fetch configs from Camera"))
        }
    }

    private fun scanNConnectFromBluetooth(
        context: Context,
        retryCallback: KFunction1<Context, Unit>
    ) {
        bleManager.context = context
        bleManager.doStartScanning(object : OnBleStatusUpdates {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                bleManager.doConnectGatt(context, result.device)
            }

            override fun onDataReceived(data: String?) {
                viewModelScope.launch(Dispatchers.IO) {
                    if (data != null) {
                        saveConfigLocally(data)
                        viewModelScope.launch {
                            _updateConfigProgress.value = Result.Success(data)
                        }
                        KeystoreHandler.storeConfigInKeystore(context, data)
                        Log.d(TAG, "Successfully Saved Configs in Keystore!")
                    } else {
                        retryCallback.invoke(context)
                    }
                }
            }

            override fun onFailedFetchConfig() {
                retryCallback.invoke(context)
            }
        })
    }

    fun saveConfigLocally(data: String) {
        val jsonObject = JSONObject(data)
        CameraInfo.discoveryUrl = jsonObject.getString("discoveryUrl")
        CameraInfo.tenantId = jsonObject.getString("tenantId")
        val backOffice = jsonObject.getString("backoffice")
        val userId = jsonObject.getString("UserID")
        CameraInfo.deviceIdFromConfig = jsonObject.getString("deviceId")
        val wiFiAPRouterMode = jsonObject.getString("WiFiAPRouterMode")
        Log.d(TAG, "Get Configs:DiscoveryUrl:" + CameraInfo.discoveryUrl)
        Log.d(TAG, "userId :$userId")
        Log.d(TAG, "deviceId :" + CameraInfo.deviceIdFromConfig)
        Log.d(TAG, "tenantId:" + CameraInfo.tenantId)
        Log.d(TAG, "wiFiAPRouterMode:$wiFiAPRouterMode")
        CameraInfo.userId = userId
        CameraInfo.wifiApRouterMode = wiFiAPRouterMode.toInt()
        if (backOffice.equals("nexus", true)) {
            CameraInfo.backOfficeType = BackOfficeType.NEXUS
        } else {
            CameraInfo.backOfficeType = BackOfficeType.COMMAND_CENTRE
        }
        enableNDisableFeaturesBasedOnBO()
    }

    private fun enableNDisableFeaturesBasedOnBO() {
        FeatureSupportHelper.supportAssociateOfficerID =
            CameraInfo.backOfficeType == BackOfficeType.COMMAND_CENTRE
    }

    fun verifyBluetoothEnabled(): Boolean {
        return bluetoothAdapter.isEnabled
    }

    fun enableBluetooth(): Boolean {
        if (!verifyBluetoothEnabled()) {
            return bluetoothAdapter.enable()
        }
        return true
    }

    fun verifyInternetConnection(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            simpleNetworkManager.verifyInternetConnection(callback)
        }
    }

    companion object {
        private const val MAX_RETRY_ATTEMPT = 3
        private const val TAG = "LoginX2ViewModel"
    }
}
