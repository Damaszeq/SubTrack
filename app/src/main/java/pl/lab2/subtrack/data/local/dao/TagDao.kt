package pl.lab2.subtrack.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import pl.lab2.subtrack.data.local.entities.Tag

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: Tag): Long

    @Query("SELECT * FROM tags WHERE name = :name")
    suspend fun getTagByName(name: String): Tag?

    @Query("SELECT * FROM tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Delete
    suspend fun deleteTag(tag: Tag)
}
