package com.minimo.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minimo.launcher.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM appInfoEntity WHERE user_handle = 0 ORDER BY LOWER(alternate_app_name)")
    fun getAllAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity WHERE is_hidden = 0 AND user_handle = 0 ORDER BY LOWER(alternate_app_name)")
    fun getAllNonHiddenAppsFlow(): Flow<List<AppInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApps(vararg apps: AppInfoEntity)

    @Query("SELECT * FROM appInfoEntity WHERE is_favourite = 1 AND user_handle = 0 ORDER BY LOWER(alternate_app_name)")
    fun getFavouriteAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("UPDATE appInfoEntity SET is_favourite = 1 WHERE class_name = :className")
    suspend fun addAppToFavourite(className: String)

    @Query("UPDATE appInfoEntity SET is_favourite = 0 WHERE class_name = :className")
    suspend fun removeAppFromFavourite(className: String)

    @Query("UPDATE appInfoEntity SET is_hidden = 1, is_favourite = 0 WHERE class_name = :className")
    suspend fun addAppToHidden(className: String)

    @Query("UPDATE appInfoEntity SET is_hidden = 0 WHERE class_name = :className")
    suspend fun removeAppFromHidden(className: String)

    @Query("UPDATE appInfoEntity SET alternate_app_name = :newName WHERE class_name = :className")
    suspend fun renameApp(className: String, newName: String)

    @Query("DELETE FROM appInfoEntity WHERE class_name = :className")
    suspend fun deleteApp(className: String)
}