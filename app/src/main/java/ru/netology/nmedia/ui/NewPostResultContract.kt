package ru.netology.nmedia.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.model.Post

const val POST_KEY_EXTRA = "POST"
class NewPostResultContract : ActivityResultContract<Post?, Post?>() {

    override fun createIntent(context: Context, input: Post?): Intent =
        Intent(context, NewPostActivity::class.java).putExtra(POST_KEY_EXTRA, input)

    override fun parseResult(resultCode: Int, intent: Intent?): Post? =
        if (resultCode == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra(POST_KEY_EXTRA, Post::class.java)
            } else {
                intent?.getParcelableExtra(POST_KEY_EXTRA)
            }

        } else {
            null
        }
}