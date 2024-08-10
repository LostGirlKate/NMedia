package ru.netology.nmedia.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.error.ErrorType
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.util.UIHelper
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private lateinit var binding: FragmentFeedBinding

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFeedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearEdited()
        initRecycleView()
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

            override fun onLocalPostSend() {
                viewModel.sendAllLocalPosts()
            }

            override fun onLocalDelete(post: Post) {
                viewModel.deleteLocal(post)
            }

            override fun onShare(post: Post) {
                /*val data =
                    if (post.video.isEmpty()) post.content else post.content + "     " + post.video*/
                val data = post.content
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
                /*val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.video))
                startActivity(intent)*/
            }

            override fun onPostClick(post: Post) {
                viewModel.viewPost(post)
                findNavController().navigate(R.id.action_feedFragment_to_postFragment)
            }
        })
        list.adapter = adapter
        list.setItemAnimator(null)
        viewModel.dataState.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading
            if (state.error) {
                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.retry_loading) {
                        when (state.errorType) {
                            ErrorType.SAVE_ERROR -> viewModel.saveAfterError()
                            ErrorType.LIKE_ERROR -> viewModel.likeByIdAfterError()
                            ErrorType.GET_DATA_ERROR -> viewModel.loadPosts()
                            ErrorType.DELETE_ERROR -> viewModel.removeByIdAfterError()
                            null -> viewModel.loadPosts()
                        }

                    }
                    .setAnchorView(binding.fab)
                    .setDuration(8000)
                    .show()
            }
        }
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.emptyText.isVisible = state.empty
        }

        viewModel.edited.observe(viewLifecycleOwner) {
            if (it.id > 0) findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        viewModel.showErrorWindow.observe(viewLifecycleOwner) {
            val dialog = UIHelper.alertErrorDialog(requireContext(), it)
            dialog.show()
        }

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.SwipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshPosts()
            binding.SwipeRefreshLayout.isRefreshing = false
        }
    }

}