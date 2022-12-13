package com.wing.tree.bruni.inPlaceTranslate.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wing.tree.bruni.inPlaceTranslate.data.dao.TranslationDao
import com.wing.tree.bruni.inPlaceTranslate.data.entity.Translation

@androidx.room.Database(
    entities = [Translation::class],
    exportSchema = false,
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract val translationDao: TranslationDao

    companion object {
        private const val PACKAGE_NAME = "com.wing.tree.bruni.inPlaceTranslate.data.database.archive"
        private const val CLASS_NAME = "Database"
        private const val NAME = "$PACKAGE_NAME.$CLASS_NAME"
        private const val VERSION = "1.0.0"

        @Volatile
        private var instance: Database? = null

        fun instance(context: Context): Database {
            synchronized(this) {
                return instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    "$NAME.$VERSION"
                )
                    .build()
                    .also {
                        instance = it
                    }
            }
        }
    }
}