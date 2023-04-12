package com.lawmobile.presentation.ui.onBoardingCards

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.lawmobile.presentation.R
import com.lawmobile.presentation.databinding.ActivityOnBoardingCardsBinding
import com.lawmobile.presentation.entities.OnBoardingCardContent
import com.lawmobile.presentation.extensions.dataStore
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.ui.login.x2.LoginX2Activity
import com.lawmobile.presentation.utils.Constants.ON_BOARDING_DISPLAYED
import kotlinx.coroutines.launch

class OnBoardingCardsActivity : BaseActivity() {

    override val parentTag: String
        get() = this::class.java.simpleName

    private lateinit var adapter: OnBoardingCardsAdapter
    private lateinit var binding: ActivityOnBoardingCardsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnBoardingCardsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.setAdapter()
        setupIndicators()
        binding.setCurrentIndicator(0)
        binding.setListeners()
    }

    private fun ActivityOnBoardingCardsBinding.setAdapter() {
        adapter = OnBoardingCardsAdapter(
            listOf(
                OnBoardingCardContent(
                    getString(R.string.description_card_1),
                    R.drawable.ob_card_1
                ),
                OnBoardingCardContent(
                    getString(R.string.description_card_2),
                    R.drawable.ob_card_2
                ),
                OnBoardingCardContent(
                    getString(R.string.description_card_3),
                    R.drawable.ob_card_3
                ),
                OnBoardingCardContent(
                    getString(R.string.description_card_4),
                    R.drawable.ob_card_4
                ),
                OnBoardingCardContent(
                    getString(R.string.description_card_5),
                    R.drawable.ob_card_5
                )
            )
        )

        introSliderViewPager.adapter = adapter

        introSliderViewPager.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setCurrentIndicator(position)
                    showButtonsDependingOnCard(position)
                }
            })
    }

    private fun ActivityOnBoardingCardsBinding.showButtonsDependingOnCard(position: Int) {
        textViewSkip.isVisible = position != adapter.itemCount - 1
        buttonStartNow.isVisible = !textViewSkip.isVisible
    }

    private fun ActivityOnBoardingCardsBinding.setListeners() {
        buttonNextListener()
        buttonSkipListener()
    }

    private fun ActivityOnBoardingCardsBinding.buttonSkipListener() {
        textViewSkip.setOnClickListener { goToLoginActivity() }
    }

    private fun ActivityOnBoardingCardsBinding.buttonNextListener() {
        buttonStartNow.setOnClickListener { goToLoginActivity() }
    }

    private fun goToLoginActivity() {
        setOnBoardingDisplayPreference()
        val loginActivityIntent = Intent(applicationContext, LoginX2Activity::class.java)
        startActivity(loginActivityIntent)
        finish()
    }

    private fun setOnBoardingDisplayPreference() {
        lifecycleScope.launch {
            baseContext.dataStore.edit { settings ->
                settings[ON_BOARDING_DISPLAYED] = true
            }
        }
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(adapter.itemCount)
        val layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT).apply {
            setMargins(12, 0, 12, 0)
        }

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun ActivityOnBoardingCardsBinding.setCurrentIndicator(index: Int) {
        val childCount = indicatorContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }
}
