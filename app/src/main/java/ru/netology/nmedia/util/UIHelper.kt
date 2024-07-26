package ru.netology.nmedia.util

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CustomErrorDialogBinding

object UIHelper {
    fun alertErrorDialog(context: Context, errorText: String): AlertDialog {
        val binding = CustomErrorDialogBinding.inflate(LayoutInflater.from(context))
        binding.errorText.text = context.getString(
            R.string.error,
            errorText
        )
        val dialogBuilder = MaterialAlertDialogBuilder(
            context,
            R.style.MaterialAlertDialog_rounded
        )
            .setView(binding.root)
            .setCancelable(true)
        return dialogBuilder.create()
    }
}