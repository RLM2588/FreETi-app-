package com.example.freeti.data_base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.freeti.data.local.entity.DAvatar
import com.example.freeti.data.local.entity.DContacts
import com.example.freeti.data.local.entity.DGroupEvents
import com.example.freeti.data.local.entity.DGroups
import com.example.freeti.data.local.entity.DGroupsUsers
import com.example.freeti.data.local.entity.DOtherTasks
import com.example.freeti.data.local.entity.DPushTemplate
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.data.local.entity.DVote
import com.example.freeti.data.local.entity.DVoting
import com.example.freeti.sync.SyncMetadata

@Database(
    entities = [DAvatar::class, DContacts::class, DOtherTasks::class, DGroupEvents::class,
        DGroups::class, DGroupsUsers::class, DPushTemplate::class,
        DTasks::class, DUsers::class, DVote::class, DVoting::class, SyncMetadata::class],
    version = 5,
    exportSchema = true // уточнить что это, но знаю что это что-то для миграции
)

//@TypeConverters(ConvertersEnum::class) - отказ от конвертеров, не работают на Kotlin
abstract class AppDataBase : RoomDatabase(){
    abstract fun tasksDao(): TasksDao
    abstract fun myTasksDao(): MyTasksDao
    abstract fun usersDao(): UserDao
    abstract fun otherTaskDao(): OtherTasksDao
    abstract fun groupsDao(): GroupsDao
    abstract fun syncMetadataDao(): SyncMetadataDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "freeti_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}