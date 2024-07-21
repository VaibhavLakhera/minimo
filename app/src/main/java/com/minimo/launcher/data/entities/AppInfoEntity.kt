package com.minimo.launcher.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appInfoEntity")
data class AppInfoEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "alternate_app_name", defaultValue = "")
    val alternateAppName: String,

    @ColumnInfo(name = "is_favourite", defaultValue = "0")
    val isFavourite: Boolean,

    @ColumnInfo(name = "is_hidden", defaultValue = "0")
    val isHidden: Boolean
)