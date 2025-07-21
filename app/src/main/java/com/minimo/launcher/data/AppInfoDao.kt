package com.minimo.launcher.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minimo.launcher.data.entities.AppInfoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM appInfoEntity ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    fun getAllAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    suspend fun getAllApps(): List<AppInfoEntity>

    @Query("SELECT * FROM appInfoEntity WHERE is_hidden = 0 ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    fun getAllNonHiddenAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("SELECT * FROM appInfoEntity WHERE package_name = :packageName")
    suspend fun getAppsByPackageName(packageName: String): List<AppInfoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addApps(apps: List<AppInfoEntity>)

    @Query("SELECT * FROM appInfoEntity WHERE is_favourite = 1 ORDER BY COALESCE( NULLIF(alternate_app_name, ''), app_name ) COLLATE NOCASE")
    fun getFavouriteAppsFlow(): Flow<List<AppInfoEntity>>

    @Query("UPDATE appInfoEntity SET is_favourite = 1 WHERE class_name = :className AND package_name = :packageName AND user_handle = :userHandle")
    suspend fun addAppToFavourite(className: String, packageName: String, userHandle: Int)

    @Query("UPDATE appInfoEntity SET is_favourite = 0 WHERE class_name = :className AND package_name = :packageName AND user_handle = :userHandle")
    suspend fun removeAppFromFavourite(className: String, packageName: String, userHandle: Int)

    @Query("UPDATE appInfoEntity SET is_hidden = 1, is_favourite = 0 WHERE class_name = :className AND package_name = :packageName AND user_handle = :userHandle")
    suspend fun addAppToHidden(className: String, packageName: String, userHandle: Int)

    @Query("UPDATE appInfoEntity SET is_hidden = 0 WHERE class_name = :className AND package_name = :packageName AND user_handle = :userHandle")
    suspend fun removeAppFromHidden(className: String, packageName: String, userHandle: Int)

    @Query("UPDATE appInfoEntity SET alternate_app_name = :newName WHERE class_name = :className AND package_name = :packageName")
    suspend fun renameApp(className: String, packageName: String, newName: String)

    @Query("DELETE FROM appInfoEntity WHERE class_name = :className AND package_name = :packageName AND user_handle = :userHandle")
    suspend fun deleteAppByClassAndPackage(className: String, packageName: String, userHandle: Int)

    @Query("DELETE FROM appInfoEntity WHERE package_name = :packageName AND user_handle = :userHandle")
    suspend fun deleteAppByPackage(packageName: String, userHandle: Int)
}