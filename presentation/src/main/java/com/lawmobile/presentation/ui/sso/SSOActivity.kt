package com.lawmobile.presentation.ui.sso

import android.content.RestrictionsManager
import android.os.Bundle
import androidx.activity.viewModels
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivitySsoLoginBinding
import com.lawmobile.presentation.extensions.attachFragment
import com.lawmobile.presentation.ui.base.BaseActivity
import com.safefleet.mobile.authentication.sso.SSOLoginFragment

class SSOActivity : BaseActivity() {

    private val viewModel: SSOViewModel by viewModels()

    private lateinit var binding: ActivitySsoLoginBinding

    private val ssoLoginFragment by lazy {
        SSOLoginFragment.createInstance(viewModel.getNetworkManager())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySsoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        attachSSOFragment()
        binding.setListeners()
    }

    private fun ActivitySsoLoginBinding.setListeners() {
        returnButton.setOnClickListener {
            setResult(RestrictionsManager.RESULT_ERROR_INTERNAL)
            finish()
        }
    }

    private fun attachSSOFragment() {
        supportFragmentManager.attachFragment(
            R.id.ssoLoginFragment,
            ssoLoginFragment,
            SSOLoginFragment.TAG
        )
    }
}