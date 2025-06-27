package com.minimo.launcher.data

import android.content.Context
import android.content.pm.LauncherActivityInfo
import android.content.pm.LauncherApps
import android.os.Process
import android.os.UserManager
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

object DatabaseMigrations {
    fun MIGRATION_1_2(context: Context) = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create new table with updated schema
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `appInfoEntity_new` (
                    `package_name` TEXT NOT NULL,
                    `user_handle` INTEGER NOT NULL,
                    `app_name` TEXT NOT NULL,
                    `class_name` TEXT NOT NULL,
                    `alternate_app_name` TEXT NOT NULL DEFAULT '',
                    `is_favourite` INTEGER NOT NULL DEFAULT 0,
                    `is_hidden` INTEGER NOT NULL DEFAULT 0,
                    PRIMARY KEY(`package_name`, `user_handle`, `class_name`)
                )
            """.trimIndent()
            )

            // Using LauncherApps get all activities. This will be used to get the className of existing apps in DB
            val launcherApps =
                context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
            val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
            val mainUserHandle = Process.myUserHandle()

            // Build a map of package names to their launcher activities
            val packageToActivities = mutableMapOf<String, LauncherActivityInfo>()

            try {
                for (profile in userManager.userProfiles) {
                    val activities = launcherApps.getActivityList(null, profile)
                    for (activity in activities) {
                        val packageName = activity.componentName.packageName
                        packageToActivities[packageName] = activity
                    }
                }
            } catch (exception: Exception) {
                Timber.e(exception)
            }

            // Get existing data from old table
            val cursor = db.query("SELECT * FROM appInfoEntity")

            while (cursor.moveToNext()) {
                val packageName = cursor.getString(cursor.getColumnIndexOrThrow("package_name"))
                val appName = cursor.getString(cursor.getColumnIndexOrThrow("app_name"))
                val alternateAppName =
                    cursor.getString(cursor.getColumnIndexOrThrow("alternate_app_name"))
                val isFavourite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favourite"))
                val isHidden = cursor.getInt(cursor.getColumnIndexOrThrow("is_hidden"))

                // Find the actual launcher activity for this package
                val activity = packageToActivities[packageName]

                // Insert an entry for each launcher activity found
                if (activity != null) {
                    // Only add main profile activities during migration
                    if (activity.user == mainUserHandle) {
                        val className = activity.componentName.className
                        val userHandle = activity.user.hashCode()

                        db.execSQL(
                            """
                                INSERT OR REPLACE INTO `appInfoEntity_new` 
                                (`package_name`, `user_handle`, `app_name`, `class_name`, `alternate_app_name`, `is_favourite`, `is_hidden`)
                                VALUES (?, ?, ?, ?, ?, ?, ?)
                            """,
                            arrayOf(
                                packageName,
                                userHandle,
                                appName,
                                className,
                                alternateAppName,
                                isFavourite,
                                isHidden
                            )
                        )
                    }
                }
            }
            cursor.close()

            // Drop old table
            db.execSQL("DROP TABLE `appInfoEntity`")

            // Rename new table to original name
            db.execSQL("ALTER TABLE `appInfoEntity_new` RENAME TO `appInfoEntity`")
        }
    }
}