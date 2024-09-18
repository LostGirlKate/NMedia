package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.databinding.FragmentViewImageBinding
import ru.netology.nmedia.util.load
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class ViewImageFragment : Fragment() {
    private lateinit var binding: FragmentViewImageBinding
    private val viewModel: PostViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentViewImageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearEdited()
        initData()
    }

    private fun initData() {
        viewModel.data.observe(viewLifecycleOwner) { state ->
            val post = state.posts.firstOrNull { it.id == viewModel.getFilterPostID() }
            if (post != null) {
                binding.postImage.load("${BuildConfig.BASE_URL}/media/${post.attachment?.url}")
            }
        }
    }

}