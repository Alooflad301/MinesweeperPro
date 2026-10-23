package com.example.minesweeper

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class ResultDialogFragment : DialogFragment() {

    companion object {
        const val TAG = "result_dialog"

        private const val ARG_IS_WIN = "arg_is_win"
        private const val ARG_TIME = "arg_time"
        private const val ARG_LEVEL = "arg_level"

        fun newInstance(
            isWin: Boolean,
            timeSeconds: Int,
            levelName: String
        ): ResultDialogFragment {
            return ResultDialogFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_WIN, isWin)
                    putInt(ARG_TIME, timeSeconds)
                    putString(ARG_LEVEL, levelName)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val isWin = requireArguments().getBoolean(ARG_IS_WIN)
        val timeSeconds = requireArguments().getInt(ARG_TIME)
        val levelName = requireArguments().getString(ARG_LEVEL) ?: Level.EASY.name

        val message = if (isWin) {
            val level = Level.valueOf(levelName)

            val isNewRecord = RecordRepository(requireContext())
                .saveRecordIfBetter(level, timeSeconds)

            if (isNewRecord) {
                "Победа!\nВремя: $timeSeconds с\nНовый рекорд!"
            } else {
                "Победа!\nВремя: $timeSeconds с"
            }
        } else {
            "Поражение.\nВремя: $timeSeconds с"
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(if (isWin) "Игра окончена" else "Мина!")
            .setMessage(message)
            .setPositiveButton("ОК", null)
            .create()
    }
}