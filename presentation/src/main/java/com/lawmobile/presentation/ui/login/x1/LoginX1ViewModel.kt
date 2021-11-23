package com.lawmobile.presentation.ui.login.x1

import com.lawmobile.domain.usecase.getUserFromCamera.GetUserFromCamera
import com.lawmobile.presentation.connectivity.WifiHelper
import com.lawmobile.presentation.ui.login.LoginBaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class LoginX1ViewModel @Inject constructor(
    getUserFromCamera: GetUserFromCamera,
    wifiHelper: WifiHelper,
    ioDispatcher: CoroutineDispatcher
) : LoginBaseViewModel(getUserFromCamera, wifiHelper, ioDispatcher)
