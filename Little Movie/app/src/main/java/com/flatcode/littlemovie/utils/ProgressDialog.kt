package com.flatcode.littlemovie.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.flatcode.littlemovie.databinding.DialogProgressBinding

class ProgressDialog(context: Context) {

    private val dialog: Dialog = Dialog(context)
    private val binding: DialogProgressBinding =
        DialogProgressBinding.inflate(LayoutInflater.from(context))

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun setTitle(title: CharSequence?): ProgressDialog {
        if (!title.isNullOrEmpty()) {
            binding.textTitle.text = title
            binding.textTitle.visibility = View.VISIBLE
        } else {
            binding.textTitle.visibility = View.GONE
        }
        return this
    }

    fun setMessage(message: CharSequence?): ProgressDialog {
        if (!message.isNullOrEmpty()) {
            binding.textMessage.text = message
            binding.textMessage.visibility = View.VISIBLE
        } else {
            binding.textMessage.visibility = View.GONE
        }
        return this
    }

    fun setCanceledOnTouchOutside(cancel: Boolean): ProgressDialog {
        dialog.setCanceledOnTouchOutside(cancel)
        return this
    }

    fun setCancelable(cancel: Boolean): ProgressDialog {
        dialog.setCancelable(cancel)
        return this
    }

    fun show() {
        try {
            if (!dialog.isShowing) {
                dialog.show()
            }
        } catch (_: Exception) {
        }
    }

    fun dismiss() {
        try {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        } catch (_: Exception) {
        }
    }

    val isShowing: Boolean
        get() = dialog.isShowing
}