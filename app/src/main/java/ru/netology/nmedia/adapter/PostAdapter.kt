package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post

interface OnInteractionListener {
    fun onLike(post: Post) {}
    // УДАЛЕНО: fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
}

val BASE_URL = "http://10.0.2.2:9999"

class PostAdapter(
    private val onInteractionListener: OnInteractionListener,
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        getAvatars(post, binding)

        if (!post.savedOnServer) {
            binding.like.visibility = View.INVISIBLE
            binding.share.visibility = View.INVISIBLE
        } else {
            binding.like.visibility = View.VISIBLE
            binding.share.visibility = View.VISIBLE
        }

        if (post.savedOnServer) {
            binding.savedOnServer.setImageResource(R.drawable.ic_baseline_public_24)
        } else {
            binding.savedOnServer.setImageResource(R.drawable.ic_baseline_public_off_24)
        }

        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content
            like.isChecked = post.likedByMe
            like.text = "${post.likes}"

            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)
                    // УДАЛЕН пункт Edit из меню
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)
                                true
                            }
                            else -> false
                        }
                    }
                }.show()
            }

            like.setOnClickListener {
                onInteractionListener.onLike(post)
            }

            share.setOnClickListener {
                onInteractionListener.onShare(post)
            }
        }
    }
}

fun getAvatars(post: Post, binding: CardPostBinding) {
    Glide.with(binding.avatar)
        .load("$BASE_URL/avatars/${post.authorAvatar}")
        .placeholder(R.drawable.ic_baseline_man_24)
        .error(R.drawable.ic_baseline_cancel_24)
        .circleCrop()
        .timeout(10_000)
        .into(binding.avatar)
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}