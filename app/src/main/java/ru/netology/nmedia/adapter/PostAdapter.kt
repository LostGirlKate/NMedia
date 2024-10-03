package ru.netology.nmedia.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.databinding.TimeCardBinding
import ru.netology.nmedia.model.Ad
import ru.netology.nmedia.model.FeedItem
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.model.TimeSeparator
import ru.netology.nmedia.util.load
import ru.netology.nmedia.util.loadCircleCrop
import ru.netology.nmedia.util.toDisplayString


interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onShare(post: Post) {}
    fun onPlay(post: Post) {}
    fun onPostClick(post: Post) {}
    fun onLocalPostSend() {}
    fun onLocalDelete(post: Post) {}
    fun onShowImage(post: Post) {}
}


class PostsAdapter(
    private val onInteractionListener: OnInteractionListener,
) : PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            is TimeSeparator -> R.layout.time_card
            null -> {
                Log.d("unknown", "${getItem(position)}")
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_ad -> {
                val binding = CardAdBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AdViewHolder(binding)
            }

            R.layout.card_post -> {
                val binding = CardPostBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return PostViewHolder(binding, onInteractionListener)
            }

            R.layout.time_card -> {
                val binding = TimeCardBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )

                return TimeSeparatorViewHolder(binding)
            }

            else -> error("unknown item type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as? PostViewHolder)?.bind(item)
            is TimeSeparator -> (holder as? TimeSeparatorViewHolder)?.bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {
        binding.image.load("${BuildConfig.BASE_URL}/media/${ad.image}")
    }
}

class TimeSeparatorViewHolder(
    private val binding: TimeCardBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(timeSeparator: TimeSeparator) {
        binding.title.text = timeSeparator.title
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val onInteractionListener: OnInteractionListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            avatar.loadCircleCrop("${BuildConfig.BASE_URL}/avatars/${post.authorAvatar}")
            author.text = post.author
            published.text = post.published.toString()
            content.text = post.content
            likeButton.text = post.likes.toDisplayString()
            likeButton.isChecked = post.likedByMe
//            shareButton.text = post.shareCount.toDisplayString()
//            viewCount.text = post.viewCount.toDisplayString()

            if (post.attachment != null) {
                videoGroup.visibility = View.VISIBLE
                videoImage.load("${BuildConfig.BASE_URL}/media/${post.attachment.url}")
            } else {
                videoGroup.visibility = View.GONE
            }
            videoImage.setOnClickListener { onInteractionListener.onShowImage(post) }
            //   play.setOnClickListener { onInteractionListener.onPlay(post) }
            likeButton.setOnClickListener { onInteractionListener.onLike(post) }
            shareButton.setOnClickListener { onInteractionListener.onShare(post) }
            postCard.setOnClickListener { onInteractionListener.onPostClick(post) }
            deleteLocal.setOnClickListener { onInteractionListener.onLocalDelete(post) }
            menu.setIconResource(if (post.localVersion) R.drawable.ic_local_version else R.drawable.more_vert_icon)
            likeButton.isEnabled = !post.localVersion
            shareButton.isEnabled = !post.localVersion
            deleteLocal.visibility =
                if (post.localVersion && !post.blockForDelete && post.isForInsert) View.VISIBLE else View.GONE
            menu.visibility = if (post.ownedByMe) View.VISIBLE else View.INVISIBLE
            menu.setOnClickListener {
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

}

class PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        return oldItem == newItem
    }

}