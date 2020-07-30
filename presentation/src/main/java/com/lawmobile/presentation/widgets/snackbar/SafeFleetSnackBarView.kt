package com.lawmobile.presentation.widgets.snackbar

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.ContentViewCallback
import com.lawmobile.presentation.R
import kotlinx.android.synthetic.main.safefleet_snackbar_view.view.*

class SafeFleetSnackBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), ContentViewCallback {

    var tvMsg: TextView
    var imLeft: ImageView
    var layRoot: ConstraintLayout

    init {
        View.inflate(context, R.layout.safefleet_snackbar_view, this)
        clipToPadding = false
        tvMsg = findViewById(R.id.textViewCustomSnackBarResult)
        imLeft = findViewById(R.id.imageViewCustomSnackBarResult)
        layRoot = findViewById(R.id.snack_constraint)
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        val scaleX = ObjectAnimator.ofFloat(imageViewCustomSnackBarResult, View.SCALE_X, 0f, 1f)
        val scaleY = ObjectAnimator.ofFloat(imageViewCustomSnackBarResult, View.SCALE_Y, 0f, 1f)
        val animatorSet = AnimatorSet().apply {
            interpolator = OvershootInterpolator()
            setDuration(500)
            playTogether(scaleX, scaleY)
        }
        animatorSet.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
    }
}