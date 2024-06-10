package ru.netology.nmedia.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.toDisplayString
import ru.netology.nmedia.viewmodel.PostViewModel


class PostFragment : Fragment() {
    private lateinit var binding: FragmentPostBinding
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPostBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearEdited()
        initData()
    }

    private fun initData() = with(binding) {

        val onInteractionListener = object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onShare(post: Post) {
                val data =
                    if (post.video.isEmpty()) post.content else post.content + "     " + post.video
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.shareById(post.id)
            }

            override fun onPlay(post: Post) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)
            }

        }

        viewModel.data.observe(viewLifecycleOwner) { posts ->
            val post = posts.first { it.id == viewModel.getFilterPostID() }
            avatar.setImageResource(post.authorAvatar)
            author.text = post.author
            published.text = post.published
            content.text = post.content
            likeButton.text = post.likeCount.toDisplayString()
            likeButton.isChecked = post.isLikedByMe
            shareButton.text = post.shareCount.toDisplayString()
            viewCount.text = post.viewCount.toDisplayString()
            videoGroup.visibility = if (post.video.isEmpty()) View.GONE else View.VISIBLE


            videoImage.setOnClickListener { onInteractionListener.onPlay(post) }
            play.setOnClickListener { onInteractionListener.onPlay(post) }
            likeButton.setOnClickListener { onInteractionListener.onLike(post) }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }

                            R.id.edit -> {
                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }


        }
        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id > 0) findNavController().navigate(R.id.action_postFragment_to_newPostFragment)
        }


    }


}