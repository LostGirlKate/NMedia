package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var post: Post
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        initIcons()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initData() {
        post = Post(
            1,
            "Нетология. Университет интернет-профессий будущего",
            R.drawable.ic_netology_48dp,
            "21 мая в 18:36",
            "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
            5_099,
            9_990,
            50_695_000
        )

    }

    private fun initView() = with(binding) {
        avatar.setImageResource(post.authorAvatar)
        author.text = post.author
        published.text = post.published
        content.text = post.content
        likesCount.text = post.likeCount.toDisplayString()
        shareCount.text = post.shareCount.toDisplayString()
        viewCount.text = post.viewCount.toDisplayString()
        setLikeIcon()
    }

    private fun initIcons() = with(binding) {
        likeIcon.setOnClickListener {
            likeIconClick()
        }
        shareIcon.setOnClickListener {
            shareIconClick()
        }
    }

    private fun likeIconClick() = with(post) {
        isLikedByMe = !isLikedByMe
        if (isLikedByMe) likeCount++ else likeCount--
        binding.likesCount.text = likeCount.toDisplayString()
        setLikeIcon()
    }

    private fun shareIconClick() = with(post) {
        shareCount++
        binding.shareCount.text = shareCount.toDisplayString()
    }


    private fun setLikeIcon() = with(binding) {
        likeIcon.setImageResource(
            if (post.isLikedByMe) R.drawable.red_heart_icon else R.drawable.heart_icon
        )
    }
}