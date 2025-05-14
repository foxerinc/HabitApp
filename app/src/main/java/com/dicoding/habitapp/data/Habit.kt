package com.dicoding.habitapp.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import org.jetbrains.annotations.NotNull

//TODO 1 : Define a local database table using the schema in app/schema/habits.json
@Entity(tableName = "habits")
@Parcelize
data class Habit(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    @NotNull
    val title: String,
    @ColumnInfo(name = "minutesFocus")
    @NotNull
    val minutesFocus: Long,
    @ColumnInfo(name = "startTime")
    @NotNull
    val startTime: String,
    @ColumnInfo(name = "priorityLevel")
    @NotNull
    val priorityLevel: String
): Parcelable