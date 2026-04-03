package com.example.feature.user.data.data_source.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.feature.user.data.data_source.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
	@Query("SELECT * FROM users ORDER BY created_at DESC")
	fun observeUsers(): Flow<List<UserEntity>>
	
	@Query("SELECT * FROM users WHERE id = :userId")
	fun observeUserById(userId: Long): Flow<UserEntity?>
	
	@Query("SELECT * FROM users ORDER BY created_at DESC")
	suspend fun getUsers(): List<UserEntity>
	
	@Query("SELECT * FROM users WHERE id = :userId")
	suspend fun getUserById(userId: Long): UserEntity?
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertUsers(users: List<UserEntity>)
	
	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertUser(user: UserEntity)
	
	@Update
	suspend fun updateUser(user: UserEntity)
	
	@Query("DELETE FROM users WHERE id = :userId")
	suspend fun deleteUser(userId: Long)
	
	@Query("DELETE FROM users")
	suspend fun clearAll()
	
	@Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%'")
	suspend fun searchUser(query: String): List<UserEntity>
}


