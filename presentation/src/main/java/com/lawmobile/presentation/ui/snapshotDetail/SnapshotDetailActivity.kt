package com.lawmobile.presentation.ui.snapshotDetail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.lawmobile.presentation.R
import com.lawmobile.presentation.extensions.convertBitmap
import com.lawmobile.presentation.extensions.createAlertProgress
import com.lawmobile.presentation.extensions.setOnClickListenerCheckConnection
import com.lawmobile.presentation.extensions.showToast
import com.lawmobile.presentation.ui.base.BaseActivity
import com.lawmobile.presentation.utils.Constants
import com.safefleet.mobile.avml.cameras.entities.CameraConnectFile
import com.safefleet.mobile.commons.helpers.Result
import kotlinx.android.synthetic.main.activity_file_list.textViewFileListBack
import kotlinx.android.synthetic.main.activity_snapshot_item_detail.*
import java.lang.Exception
import javax.inject.Inject

class SnapshotDetailActivity : BaseActivity() {

    @Inject
    lateinit var snapshotDetailViewModel: SnapshotDetailViewModel
    private lateinit var file: CameraConnectFile
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot_item_detail)
    }

    override fun onResume() {
        super.onResume()
        createDialog()
        dialog.show()
        file = intent.getSerializableExtra(Constants.CAMERA_CONNECT_FILE) as CameraConnectFile
        snapshotDetailViewModel.getImageBytes(file)
        snapshotDetailViewModel.imageBytesLiveData.observe(
            this,
            Observer {
                configureObservers(it)
            }
        )
        configureListeners()
    }

    private fun setImageAndData(byteArray: ByteArray) {
        try {
            photoItemDetailHolder.setImageBitmap(byteArray.convertBitmap())
        } catch (e: Exception) {
            photoItemDetailHolder.setImageDrawable(
                resources.getDrawable(
                    R.drawable.ic_failed_image,
                    null
                )
            )
        }
        sizeValue.text = byteArray.size.toString()
        photoNameValue.text = file.name
        dateTimeValue.text = file.date
        dialog.hide()
    }

    private fun createDialog() {
        dialog = this.createAlertProgress()
    }

    private fun configureListeners() {
        textViewFileListBack.setOnClickListenerCheckConnection {
            onBackPressed()
        }
    }

    private fun configureObservers(result: Result<ByteArray>) {
        when (result) {
            is Result.Success -> {
                setImageAndData(result.data)
            }
            is Result.Error -> {
                this.showToast(getString(R.string.snapshot_detail_load_failed), Toast.LENGTH_LONG)
            }
        }
    }
}
