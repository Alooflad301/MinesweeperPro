package com.example.minesweeper

import android.content.Context

class RecordRepository(context: Context) {

    private val preferences = context.getSharedPreferences(
        "minesweeper_records",
        Context.MODE_PRIVATE
    )

    fun getRecord(level: Level): Int {
        return preferences.getInt(level.name, Int.MAX_VALUE)
    }

    fun saveRecordIfBetter(
        level: Level,
        timeSeconds: Int
    ): Boolean {
        val oldRecord = getRecord(level)

        return if (timeSeconds < oldRecord) {
            preferences.edit()
                .putInt(level.name, timeSeconds)
                .apply()

            true
        } else {
            false
        }
    }
}