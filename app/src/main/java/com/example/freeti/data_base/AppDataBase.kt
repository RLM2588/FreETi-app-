package com.example.freeti.data_base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.freeti.data.local.entity.Converters
import com.example.freeti.data.local.entity.DAvatar
import com.example.freeti.data.local.entity.DContacts
import com.example.freeti.data.local.entity.DEditedTasks
import com.example.freeti.data.local.entity.DGroupEvents
import com.example.freeti.data.local.entity.DGroups
import com.example.freeti.data.local.entity.DGroupsUsers
import com.example.freeti.data.local.entity.DPushTemplate
import com.example.freeti.data.local.entity.DRepeatTasks
import com.example.freeti.data.local.entity.DTasks
import com.example.freeti.data.local.entity.DUsers
import com.example.freeti.data.local.entity.DVote
import com.example.freeti.data.local.entity.DVoting

@Database(
    entities = [DAvatar::class, DContacts::class, DEditedTasks::class, DGroupEvents::class,
        DGroups::class, DGroupsUsers::class, DPushTemplate::class, DRepeatTasks::class,
        DTasks::class, DUsers::class, DVote::class, DVoting::class],
    version = 1,
    exportSchema = true // уточнить что это, но знаю что это что-то для миграции
)

@TypeConverters(Converters::class)
abstract class AppDataBase : RoomDatabase(){
    abstract fun dataBaseDao(): DataBaseDao

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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}