package com.example.freeti.data.local.entity
import androidx.room.TypeConverter
import com.example.freeti.enum.EPrivacy
import com.example.freeti.enum.ERole
import com.example.freeti.enum.EStatus

// Это обычный класс, который умеет превращать Enum в String и обратно
class Converters {
    // в строку
    @TypeConverter
    fun fromStatusType(value: EStatus): String {
        return value.name
    }

    @TypeConverter
    fun fromRoleType(value: ERole): String {
        return value.name
    }

    @TypeConverter
    fun fromPrivacyType(value: EPrivacy): String {
        return value.name
    }

    // из строки
    @TypeConverter
    fun toStatusType(value: String): EStatus {
        return enumValues<EStatus>().find { it.name == value } ?: EStatus.IN_PROGRESS
    }

    @TypeConverter
    fun toRoleType(value: String): ERole {
        return enumValues<ERole>().find { it.name == value } ?: ERole.USER
    }

    @TypeConverter
    fun toPrivacyType(value: String): EPrivacy {
        return enumValues<EPrivacy>().find { it.name == value } ?: EPrivacy.PRIVATE
    }
}