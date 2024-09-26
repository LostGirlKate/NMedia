package ru.netology.nmedia.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.databinding.FragmentPostBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.toDisplayString
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                viewModel.likeById(post)
            }

            override fun onLocalPostSend() {
                viewModel.sendAllLocalPosts()
            }

            override fun onLocalDelete(post: Post) {
                viewModel.deleteLocal(post)
            }


            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                findNavController().navigateUp()
            }

            override fun onShowImage(post: Post) {
                viewModel.viewPost(post)
                findNavController().navigate(R.id.action_postFragment_to_viewImageFragment)
            }

            override fun onShare(post: Post) {
                val data = post.content
                // if (post.video.isEmpty()) post.content else post.content + "     " + post.video
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, data)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
//                viewModel.shareById(post.id)
            }

            override fun onPlay(post: Post) {
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
//                startActivity(intent)
            }

        }

        viewModel.singlePost.observe(viewLifecycleOwner) { post ->
            author.text = post?.author
            published.text = post?.published.toString()
            content.text = post?.content
            likeButton.text = post?.likes?.toDisplayString()
            likeButton.isChecked = post?.likedByMe == true
//            shareButton.text = post.shareCount.toDisplayString()
//            viewCount.text = post.viewCount.toDisplayString()

            if (post != null) {
                val avatarUrl = "http://10.0.2.2:9999/avatars/${post.authorAvatar}"
                Glide.with(avatar)
                    .load(avatarUrl)
                    .transform(CircleCrop())
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)
                    .timeout(10_000)
                    .into(avatar)
                if (post.attachment != null) {
                    val attachmentUrl = "http://10.0.2.2:9999/media/${post.attachment.url}"
                    videoGroup.visibility = View.VISIBLE
                    Glide.with(videoImage)
                        .load(attachmentUrl)
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                        .timeout(10_000)
                        .into(videoImage)
                } else {
                    videoGroup.visibility = View.GONE
                }
                videoImage.setOnClickListener { onInteractionListener.onShowImage(post) }
//                play.setOnClickListener { onInteractionListener.onPlay(post) }
                likeButton.setOnClickListener { onInteractionListener.onLike(post) }
                shareButton.setOnClickListener { onInteractionListener.onShare(post) }
                menu.setIconResource(if (post.localVersion) R.drawable.ic_local_version else R.drawable.more_vert_icon)
                likeButton.isEnabled = !post.localVersion
                shareButton.isEnabled = !post.localVersion
                deleteLocal.visibility =
                    if (post.localVersion && !post.blockForDelete && post.isForInsert) View.VISIBLE else View.GONE
                menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            }
            menu.setOnClickListener {
                if (post != null) {
                    if (!post.localVersion) {
                        PopupMenu(it.context, it).apply {
                            inflate(R.menu.post_menu)
                            menu.setGroupVisible(R.id.owned, post.ownedByMe)
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
                    } else {
                        onInteractionListener.onLocalPostSend()
                    }
                }
            }
        }

        viewModel.edited.observe(viewLifecycleOwner)
        {
            if (it.id > 0) findNavController().navigate(R.id.action_postFragment_to_newPostFragment)
        }


    }


}