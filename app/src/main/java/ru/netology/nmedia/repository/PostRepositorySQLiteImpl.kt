package ru.netology.nmedia.repository


/*
class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {
    private var posts = emptyList<Post>()


    override fun getAll(): LiveData<List<Post>> = dao.getAll().map {
        it.map (
            PostEntity::toDto
        )
    }

    override fun save(post: Post) {
        dao.save(PostEntity.fromDto(post))
    }

    override fun likeById(id: Long) {
        dao.likedById(id)
    }

    override fun shareById(id: Long) {

    }

    override fun removeById(id: Long) {
        dao.removeById(id)

    }
*/
