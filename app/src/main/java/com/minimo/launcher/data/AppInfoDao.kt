package com.minimo.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minimo.launcher.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM appInfoEntity ORDER BY LOWER(alternate_app_name)")
    fun getAllAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity WHERE is_hidden = 0 ORDER BY LOWER(alternate_app_name)")
    fun getAllNonHiddenAppsFlow(): Flow<List<AppInfoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApps(vararg apps: AppInfoEntity)

    @Query("SELECT * FROM appInfoEntity WHERE is_favourite = 1 ORDER BY LOWER(alternate_app_name)")
    fun getFavouriteAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("UPDATE appInfoEntity SET is_favourite = 1 WHERE package_name = :packageName")
    suspend fun addAppToFavourite(packageName: String)

    @Query("UPDATE appInfoEntity SET is_favourite = 0 WHERE package_name = :packageName")
    suspend fun removeAppFromFavourite(packageName: String)

    @Query("UPDATE appInfoEntity SET is_hidden = 1, is_favourite = 0 WHERE package_name = :packageName")
    suspend fun addAppToHidden(packageName: String)

    @Query("UPDATE appInfoEntity SET is_hidden = 0 WHERE package_name = :packageName")
    suspend fun removeAppFromHidden(packageName: String)

    @Query("UPDATE appInfoEntity SET alternate_app_name = :newName WHERE package_name = :packageName")
    suspend fun renameApp(packageName: String, newName: String)

    @Query("DELETE FROM appInfoEntity WHERE package_name = :packageName")
    suspend fun deleteApp(packageName: String)
}