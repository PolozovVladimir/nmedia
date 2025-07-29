package ru.netology.nmedia.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.PostViewModel
import java.io.File

@AndroidEntryPoint
class NewPostFragment : Fragment() {
    companion object {
        const val TEXT_ARG_KEY = "textArg"
        var Bundle.textArg: String?
            get() = getString(TEXT_ARG_KEY)
            set(value) = putString(TEXT_ARG_KEY, value)
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    private var _binding: FragmentNewPostBinding? = null
    private val binding get() = _binding!!

    private val takePhotoResult = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (!success) {
            Snackbar.make(binding.root, "Ошибка при создании фото", Snackbar.LENGTH_LONG).show()
        }
    }

    private val pickPhotoResult = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.changePhoto(it, it.toFile())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupPhotoHandlers()
        setupObservers()
        setupArguments()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.create_post_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.content.text.toString())
                        viewModel.save()
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }
                    else -> false
                }
        }, viewLifecycleOwner)
    }

    private fun setupPhotoHandlers() {
        binding.takeShotBtn.setOnClickListener {
            val uri = createImageUri()
            viewModel.changePhoto(uri, uri.toFile())
            takePhotoResult.launch(uri)
        }

        binding.pickPicBtn.setOnClickListener {
            pickPhotoResult.launch("image/*")
        }

        binding.deletePic.setOnClickListener {
            viewModel.changePhoto(null, null)
        }
    }

    private fun setupObservers() {
        viewModel.photo.observe(viewLifecycleOwner) { photoModel ->
            if (photoModel?.uri == null) {
                binding.picLayout.visibility = View.GONE
                return@observe
            }
            binding.picLayout.visibility = View.VISIBLE
            binding.pic.setImageURI(photoModel.uri)
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
    }

    private fun setupArguments() {
        arguments?.textArg?.let {
            binding.content.setText(it)
        }
    }

    private fun createImageUri(): Uri {
        val context = requireContext()
        return Uri.parse(
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                createTempFile().absolutePath,
                null,
                null
            )
        )
    }

    private fun createTempFile(): File {
        return File.createTempFile("temp_image", ".jpg", requireContext().cacheDir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}