package com.lawmobile.presentation.ui.splash

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lawmobile.domain.entities.CameraInfo
import com.lawmobile.domain.enums.CameraType
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.base.BaseViewModel
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.ui.onBoardingCards.OnBoardingCardsActivity
import com.lawmobile.presentation.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : BaseViewModel() {

    val nextActivityResult: LiveData<Class<out BaseActivity>> get() = _nextActivityResult
    private val _nextActivityResult = MediatorLiveData<Class<out BaseActivity>>()

    fun getNextActivity() {
        viewModelScope.launch {
            getCameraTypePreference()
        }
    }

    private suspend fun getCameraTypePreference() {
        val cameraType = dataStore.data
            .map { preferences ->
                preferences[Constants.CAMERA_TYPE] ?: "none"
            }.first()

        updateCameraType(cameraType)
        getOnBoardingPreference(true)
    }

    private fun updateCameraType(cameraType: String) {
        when (cameraType) {

            CameraType.X2.name -> CameraInfo.cameraType = CameraType.X2
        }
    }

    private fun wasCameraSelected(cameraType: String) =
        cameraType == CameraType.X2.name

    private suspend fun getOnBoardingPreference(wasCameraSelected: Boolean) {
        val wasOnBoardingDisplayed = dataStore.data
            .map { preferences ->
                val displayed = preferences[Constants.ON_BOARDING_DISPLAYED] ?: false
                displayed
            }.first()

        setNextActivity(wasOnBoardingDisplayed, wasCameraSelected)
    }

    private fun setNextActivity(wasOnBoardingDisplayed: Boolean, wasCameraSelected: Boolean) {
        val activity = when {
            !wasOnBoardingDisplayed -> OnBoardingCardsActivity::class.java
            else -> getCameraTypeLoginActivity()
        }
        _nextActivityResult.value = activity
    }

    private fun getCameraTypeLoginActivity() =
        LoginX2Activity::class.java
}
