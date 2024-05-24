package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.repository.PostRepositoryInMemoryImpl
import ru.netology.nmedia.util.AndroidUtils
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
        initRecycleView()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initRecycleView() = with(binding) {
        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
            }
        })
        list.adapter = adapter
        list.setItemAnimator(null)
        viewModel.data.observe(this@MainActivity) { posts ->
            adapter.submitList(posts)
        }

        viewModel.edited.observe(this@MainActivity) { post ->
            if (post.id == 0) {
                binding.editedTitleGroup.visibility = View.GONE
                return@observe
            }
            with(binding.content) {
                binding.editedTitleGroup.visibility = View.VISIBLE
                binding.postEditedTitle.text = post.content
                requestFocus()
                setText(post.content)
            }
        }

        binding.save.setOnClickListener {
            with(binding.content) {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        context.getString(R.string.error_empty_content),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

                viewModel.changeContent(text.toString())
                viewModel.save()

                clearEdited()
            }
        }

        binding.closeEdited.setOnClickListener {
            viewModel.clearEdited()
            clearEdited()
        }
    }

    private fun clearEdited() = with(binding){
        editedTitleGroup.visibility = View.GONE
        binding.postEditedTitle.text = ""
        content.setText("")
        content.clearFocus()
        AndroidUtils.hideKeyboard(content)
    }


}
