package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post


interface OnInteractionListener{
    fun onEdit(post: Post)
    fun onRemove(post: Post)
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onPlay(post: Post)
    fun onPost(post: Post)

}

class PostAdapter(
    private val listener: OnInteractionListener,

    ) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(
            binding = binding,
            listener = listener
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            videoGroup.isVisible = !post.videoLink.isNullOrBlank()
            author.text = post.author + " id: "+ post.id
            content.text = post.content

            published.text = post.published
            like.text = countFormat(post.countOfLikes)
            share.text = countFormat(post.countOfShares)


            like.isChecked = post.likedByMe
            like.setOnClickListener {
                listener.onLike(post)
            }

            share.setOnClickListener {
                listener.onShare(post)
            }

            playBtn.setOnClickListener{
                listener.onPlay(post)
            }

            root.setOnClickListener {
                listener.onPost(post)
            }


            menu.setOnClickListener { it ->
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.post_menu)

                    setOnMenuItemClickListener {menuItem ->
                        when (menuItem.itemId) {
                            R.id.remove -> {
                                listener.onRemove(post)
                                return@setOnMenuItemClickListener true
                            }
                            R.id.edit -> {
                                listener.onEdit(post)
                                return@setOnMenuItemClickListener true
                            }
                        }

                        false
                    }
                    show()
                }
            }
        }
    }
}

fun countFormat(counter: Int): String {
    val char = counter.toString().toCharArray()
    val firstSymbol = char[0]
    var secondSymbol: Char = '_'
    var thirdSymbol: Char = '_'
    if (counter > 9) secondSymbol = char[1]
    if (counter > 99) thirdSymbol = char[2]

    return when (counter) {
        in 0..999 -> counter.toString()
        in 1000..9999 -> {
            if (secondSymbol == '0') "${firstSymbol}K" else {
                "${firstSymbol}.${secondSymbol}K"
            }
        }
        in 10_000..99_999 -> "${firstSymbol}${secondSymbol}K"
        in 100_000..999_999 -> "${firstSymbol}${secondSymbol}${thirdSymbol}K"
        in 1000_000..9999_999 -> {
            if (secondSymbol == '0') "${firstSymbol}M"
            else {
                "${firstSymbol}.${secondSymbol}M"
            }
        }
        in 10_000_000..99_999_999 -> "${firstSymbol}${secondSymbol}M"
        in 100_000_000..999_999_999 -> "${firstSymbol}${secondSymbol}${thirdSymbol}M"
        else -> "infinity"
    }
}