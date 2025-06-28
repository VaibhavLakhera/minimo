package com.minimo.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minimo.launcher.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM appInfoEntity WHERE user_handle = 0 ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    fun getAllAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity WHERE is_hidden = 0 AND user_handle = 0 ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    fun getAllNonHiddenAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity WHERE package_name = :packageName AND user_handle = 0")
    suspend fun getAppsByPackageName(packageName: String): List<AppInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApps(apps: List<AppInfoEntity>)

    @Query("SELECT * FROM appInfoEntity WHERE is_favourite = 1 AND user_handle = 0 ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
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

    @Query("DELETE FROM appInfoEntity WHERE class_name IN (:classNames)")
    suspend fun deleteAppByClass(classNames: List<String>)

    @Query("DELETE FROM appInfoEntity WHERE package_name = :packageName")
    suspend fun deleteAppByPackage(packageName: String)
}