package com.example.freeti.data.local.entity
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class DTasks (
    @PrimaryKey
    val id: String,
    val title: String?,
    val body: String?,
    val user_id: Int,
    val start: Long?, //timestamp
    val end : Long?, //timestamp
    // TODO сделать enum надо + остальные поля, остановился тут, узнать как сделать конвертер и тп
    val importance: Int,
    val push_tenplate_id: Int?,
    @ColumnInfo(defaultValue = "FFFFFF")
    val colour: String,
    val created_at: Long //timestamp !!! надо ли возможность null?
    )

/*
* Table tasks {
  id uuidv7 [primary key] //128 бит. лучше почитать:
  // что такое uuidv7 https://ru.vstack.com/glossary/uuid/
  // в PostgreSQL https://habr.com/ru/companies/spring_aio/articles/946168/
  // переведенная документация https://postgrespro.ru/docs/postgresql/current/datatype-uuid
  // тест производительности https://ardentperf.com/2024/02/03/uuid-benchmark-war/
  title varchar(80)  //это уникальная строчка, размер которой ограничен числом в скобках
  body varchar(100) //это уникальная строчка, размер которой ограничен числом в скобках
  user_id integer [not null]
  start timestamp
  end timestamp
  status status //это тоже enum
  private privacy //это тоже enum
  importance integer
  push_tenplate_id integer [not null]
  colour string
  created_at timestamp
}
* */