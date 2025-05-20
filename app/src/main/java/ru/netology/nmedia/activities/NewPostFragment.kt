package ru.netology.nmedia.activities


import android.content.Context

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {


    companion object {
        var Bundle.textArg: String? by StringArg
        const val TEXT_ARG = "textArg"
    }

    val args by navArgs<NewPostFragmentArgs>()

    var savedContent: String? = "1"

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )

        val sharedPrefs = requireActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            savedContent = binding.content.text.toString()
            sharedPrefs?.edit()?.putString("SAVED_CONTENT", savedContent)?.apply()
            findNavController().navigateUp()
        }

        if (args.content.isNullOrEmpty()){
            sharedPrefs?.getString("SAVED_CONTENT", "")?.let {
                savedContent = it
                binding.content.setText(savedContent.toString())
            }
        } else binding.content.setText(args.content)


        if (!args.videoLink.isNullOrEmpty()) {
            binding.videoLink.visibility = View.VISIBLE
            binding.videoLink.setText(args.videoLink.toString())
        }
        binding.attachBtn.setOnClickListener {
            binding.videoLink.visibility = View.VISIBLE
        }

        binding.content.requestFocus()

        binding.save.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            viewModel.changeContent(
                binding.content.text.toString(),
                binding.videoLink.text.toString()
            )
            viewModel.save()
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }
        return binding.root
    }

}