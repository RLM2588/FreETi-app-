package com.example.freeti.data.local.entity
import androidx.room.TypeConverter
import com.example.freeti.enum_classes.EPrivacy
import com.example.freeti.enum_classes.ERole
import com.example.freeti.enum_classes.EStatus

// Это обычный класс, который умеет превращать Enum в String и обратно
object ConvertersEnum {
    // в строку
    //@TypeConverter
    //@JvmStatic
    //fun fromStatusType(value: EStatus): String {
    //    return value.name
    //}
//
    //@TypeConverter
    //@JvmStatic
    //fun fromRoleType(value: ERole): String {
    //    return value.name
    //}
//
    //@TypeConverter
    //@JvmStatic
    //fun fromPrivacyType(value: EPrivacy): String {
    //    return value.name
    //}
//
    //// из строки
    //@TypeConverter
    //@JvmStatic
    //fun toStatusType(value: String): EStatus {
    //    return enumValues<EStatus>().find { it.name == value } ?: EStatus.IN_PROGRESS
    //}
//
    //@TypeConverter
    //@JvmStatic
    //fun toRoleType(value: String): ERole {
    //    return enumValues<ERole>().find { it.name == value } ?: ERole.USER
    //}
//
    //@TypeConverter
    //@JvmStatic
    //fun toPrivacyType(value: String): EPrivacy {
    //    return enumValues<EPrivacy>().find { it.name == value } ?: EPrivacy.PRIVATE
    //}
}