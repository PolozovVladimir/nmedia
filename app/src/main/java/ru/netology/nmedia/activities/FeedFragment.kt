package ru.netology.nmedia.activities
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel


class FeedFragment : Fragment() {
    private val viewModel: PostViewModel by viewModels(ownerProducer = ::requireParentFragment)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(inflater, container, false)
        val adapter = PostAdapter(object : OnInteractionListener {
            override fun onEdit(post: Post) {
                viewModel.edit(post)
            }

            override fun onLike(post: Post) {
                if (post.likedByMe){
                    viewModel.dislikeById(post.id)
                } else {viewModel.likeById(post.id)}
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onShare(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }

                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }
        })

        binding.list.adapter = adapter
        viewModel.data.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.posts)
            binding.progress.isVisible = state.loading
            binding.errorGroup.isVisible = state.error
            binding.emptyText.isVisible = state.empty
            binding.swiprefresh.isRefreshing = state.loading
        }

        viewModel.requestCode.observe(viewLifecycleOwner) { requestCode ->
            toastOnError(requestCode)
        }

        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }

        binding.createBtn.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        binding.swiprefresh.setOnRefreshListener {
            viewModel.refresh()

        }

        return binding.root
    }

    private fun toastOnError(requestCode: Int) {
        if (requestCode.toString().startsWith("1")) {
            Toast.makeText(context, "Информационный код ответа", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("3")) {
            Toast.makeText(context, "Перенаправление", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("4")) {
            Toast.makeText(context, "Ошибка клиента", Toast.LENGTH_SHORT).show()
        }
        if (requestCode.toString().startsWith("5")) {
            Toast.makeText(context, "Ошибка сервера", Toast.LENGTH_SHORT).show()
        }
    }
}