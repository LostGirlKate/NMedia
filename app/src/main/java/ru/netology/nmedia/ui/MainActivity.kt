package ru.netology.nmedia.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.util.toDisplayString
import ru.netology.nmedia.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val repository by lazy {
        PostRepositoryInMemoryImpl()
    }
    private val viewModel: PostViewModel by viewModels<PostViewModel> {
        PostViewModel.PostViewModelFactory(repository = repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.data.observe(this) { post ->
            binding.avatar.setImageResource(post.authorAvatar)
            binding.author.text = post.author
            binding.published.text = post.published
            binding.content.text = post.content
            binding.likesCount.text = post.likeCount.toDisplayString()
            binding.shareCount.text = post.shareCount.toDisplayString()
            binding.viewCount.text = post.viewCount.toDisplayString()
            binding.likeIcon.setImageResource(
                if (post.isLikedByMe) R.drawable.red_heart_icon else R.drawable.heart_icon
            )
        }
        initIcons()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initIcons() = with(binding) {
        likeIcon.setOnClickListener {
            viewModel.like()
        }
        shareIcon.setOnClickListener {
            viewModel.share()
        }
    }


}