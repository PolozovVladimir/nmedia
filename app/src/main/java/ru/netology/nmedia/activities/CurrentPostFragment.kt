package ru.netology.nmedia.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import ru.netology.nmedia.R
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostViewHolder
import ru.netology.nmedia.databinding.FragmentCurrentPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel
class CurrentPostFragment: Fragment(){

   /* val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    val args by navArgs<CurrentPostFragmentArgs>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCurrentPostBinding.inflate(
            inflater,
            container,
            false
        )


        val viewHolder = PostViewHolder(binding.cardPost, object : OnInteractionListener {

            override fun onEdit(post: Post) {
                val action = CurrentPostFragmentDirections.actionCurrentPostFragmentToNewPostFragment(post.content.toString(),post.videoLink.toString())
                findNavController().navigate(action)
                viewModel.edit(post)
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)

            }

            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.sharedById(post.id)
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
            }

            @SuppressLint("QueryPermissionsNeeded")
            override fun onPlay(post: Post) {
                //                 Toast.makeText(viewLifecycleOwner, post.videoLink.toString(), Toast.LENGTH_SHORT).show()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(post.videoLink))
                //         if (intent.resolveActivity(packageManager) != null){
                startActivity(intent)
                //        }
            }

            override fun onPost(post: Post) {
                onEdit(post)
            }
        })

        viewModel.data.observe(viewLifecycleOwner){posts ->
            val post = posts.find { it.id == args.postId } ?: run{
                findNavController().navigateUp()
                return@observe
            }
            viewHolder.bind(post)
        }


        return binding.root
    }

*/}