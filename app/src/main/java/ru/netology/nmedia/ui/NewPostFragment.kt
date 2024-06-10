package ru.netology.nmedia.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel


class NewPostFragment : Fragment() {
    private lateinit var binding: FragmentNewPostBinding
    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressedCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewPostBinding.inflate(layoutInflater)


        val post = viewModel.edited.value!!
        binding.edit.setText(post.content)
        binding.linkEdit.setText(post.video)
        if (post.id > 0) {
            binding.newPostTitle.text = getString(R.string.title_edit)
        }


        binding.edit.requestFocus()
        binding.ok.setOnClickListener {
            val content = binding.edit.text.toString()
            val videoLink = binding.linkEdit.text.toString()
            viewModel.changeContent(content)
            viewModel.changeVideo(videoLink)
            viewModel.save()
            viewModel.clearEdited()
            if (post.id == 0) viewModel.clearDraft()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root
    }


    private fun addOnBackPressedCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if ((viewModel.edited.value?.id ?: 0) == 0) {
                    val content = binding.edit.text.toString()
                    val videoLink = binding.linkEdit.text.toString()
                    viewModel.saveDraft(content, videoLink)
                }

                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }
}
