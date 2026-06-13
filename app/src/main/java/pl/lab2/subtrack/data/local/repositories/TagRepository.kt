package pl.lab2.subtrack.data.local.repositories

import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.dao.TagDao
import pl.lab2.subtrack.data.local.entities.Tag

/**
 * Repository that provides insert, delete, and retrieve of [Tag] from a given data source.
 */
interface TagRepository {
    /**
     * Retrieve all the tags from the the given data source.
     */
    fun getAllTagsStream(): Flow<List<Tag>>

    /**
     * Retrieve a tag by its name.
     */
    suspend fun getTagByName(name: String): Tag?

    /**
     * Insert tag in the data source
     */
    suspend fun insertTag(tag: Tag): Long

    /**
     * Delete tag from the data source
     */
    suspend fun deleteTag(tag: Tag)
}

class OfflineTagRepository(private val tagDao: TagDao) : TagRepository {
    override fun getAllTagsStream(): Flow<List<Tag>> = tagDao.getAllTags()

    override suspend fun getTagByName(name: String): Tag? = tagDao.getTagByName(name)

    override suspend fun insertTag(tag: Tag): Long = tagDao.insertTag(tag)

    override suspend fun deleteTag(tag: Tag) = tagDao.deleteTag(tag)
}
