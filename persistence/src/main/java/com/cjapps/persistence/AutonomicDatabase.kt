package com.cjapps.persistence

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cjapps.persistence.dao.ContextDao
import com.cjapps.persistence.dao.ImageDao
import com.cjapps.persistence.dao.PlaylistDao
import com.cjapps.persistence.dao.TriggerDao
import com.cjapps.persistence.entity.Context
import com.cjapps.persistence.entity.Image
import com.cjapps.persistence.entity.Playlist
import com.cjapps.persistence.entity.Trigger
import android.content.Context as AndroidContext

@Database(
    entities = [
        Context::class,
        Image::class,
        Playlist::class,
        Trigger::class
    ],
    version = 1
)
internal abstract class AutonomicDatabase: RoomDatabase() {

    abstract fun contextDao(): ContextDao
    abstract fun imageDao(): ImageDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun triggerDao(): TriggerDao

    companion object {
        @Volatile
        private var INSTANCE: AutonomicDatabase? = null

        fun getDatabase(context: AndroidContext, runInMemory: Boolean): AutonomicDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(this) {
                val instance = if (runInMemory) {
                    Room.inMemoryDatabaseBuilder(
                        context.applicationContext,
                        AutonomicDatabase::class.java
                    )
                } else {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AutonomicDatabase::class.java,
                        "autonomic_database"
                    )
                }.build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
