package ru.netology.nmedia.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.model.Post

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(POST_KEY_EXTRA, Post::class.java)
        } else {
            intent.getParcelableExtra(POST_KEY_EXTRA)
        }
        if (post != null && post.id > 0) {
            binding.edit.setText(post.content)
            binding.linkEdit.setText(post.video)
            binding.newPostTitle.text = getString(R.string.title_edit)
        }


        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val intent = Intent()
            if (binding.edit.text.isNullOrBlank() && binding.linkEdit.text.isNullOrBlank()) {
                setResult(Activity.RESULT_CANCELED, intent)
            } else {
                val content = binding.edit.text.toString()
                val videoLink = binding.linkEdit.text.toString()
                val editedPost = post?.copy(content = content, video = videoLink)
                intent.putExtra(POST_KEY_EXTRA, editedPost)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}