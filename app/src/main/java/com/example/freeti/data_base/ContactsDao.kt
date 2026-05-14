package com.example.freeti.data_base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.freeti.data.local.entity.DContacts

@Dao
interface ContactsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(contacts: List<DContacts>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(contact: DContacts)

    @Delete
    suspend fun delete(contact: DContacts)   // Room сам найдёт по primaryKey

    @Query("SELECT * FROM contacts WHERE (user1 = :myId AND user2 = :otherId) OR (user1 = :otherId AND user2 = :myId) LIMIT 1")
    suspend fun getContactStatus(myId: Int, otherId: Int): DContacts?

    @Query("DELETE FROM contacts WHERE (user1 = :userId OR user2 = :userId) AND isFriend = 0")
    suspend fun deleteNonFriendContacts(userId: Int)

    @Query("DELETE FROM contacts WHERE user1 = :userId OR user2 = :userId")
    suspend fun deleteAllContacts(userId: Int) // если нужна полная перезапись

    // Получить всех пользователей-контактов (включая друзей) с флагом isFriend
    @Query("""
        SELECT u.*, c.isFriend FROM users u
        INNER JOIN contacts c ON (u.id = c.user1 OR u.id = c.user2)
        WHERE (c.user1 = :myId OR c.user2 = :myId) AND u.id != :myId
        ORDER BY u.username
    """)
    suspend fun getContacts(myId: Int): List<UserWithContactStatus>

    // Поиск только среди контактов (локально)
    @Query("""
        SELECT u.*, c.isFriend FROM users u
        INNER JOIN contacts c ON (u.id = c.user1 OR u.id = c.user2)
        WHERE (c.user1 = :myId OR c.user2 = :myId) AND u.id != :myId
        AND (u.username LIKE '%' || :query || '%' OR u.login LIKE '%' || :query || '%')
        ORDER BY u.username
    """)
    suspend fun searchContacts(myId: Int, query: String): List<UserWithContactStatus>
}