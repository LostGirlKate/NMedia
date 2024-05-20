package ru.netology.nmedia.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.R
import ru.netology.nmedia.model.Post

class PostRepositoryInMemoryImpl : PostRepository {
    private var post = Post(
        1,
        "Нетология. Университет интернет-профессий будущего",
        R.drawable.ic_netology_48dp,
        "21 мая в 18:36",
        "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        1,//5_099,
        2,//9_990,
        50_695_000
    )
    private val data = MutableLiveData(post)

    override fun get(): LiveData<Post> = data
    override fun like() = with(post) {
        post = copy(
            isLikedByMe = !post.isLikedByMe,
            likeCount = if (!isLikedByMe) likeCount + 1 else likeCount - 1
        )
        data.value = post
    }

    override fun share() = with(post) {
        post = copy(shareCount = shareCount + 1)
        data.value = post
    }
}