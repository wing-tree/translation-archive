package com.wing.tree.bruni.translator.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wing.tree.bruni.translator.data.dao.HistoryDao
import com.wing.tree.bruni.translator.data.dao.TranslationDao
import com.wing.tree.bruni.translator.data.entity.History
import com.wing.tree.bruni.translator.data.entity.Translation

@androidx.room.Database(
    entities = [History::class, Translation::class],
    exportSchema = false,
    version = 1
)
abstract class Database : RoomDatabase() {
    abstract val historyDao: HistoryDao
    abstract val translationDao: TranslationDao

    companion object {
        private const val PACKAGE_NAME = "com.wing.tree.bruni.inPlaceTranslate.data.database"
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