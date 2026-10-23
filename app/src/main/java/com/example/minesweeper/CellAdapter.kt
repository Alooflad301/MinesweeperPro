package com.example.minesweeper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class CellAdapter(
    private val context: Context,
    private var board: Board
) : BaseAdapter() {

    fun updateBoard(newBoard: Board) {
        board = newBoard
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return board.cells.size
    }

    override fun getItem(position: Int): Cell {
        return board.cells[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater
            .from(context)
            .inflate(R.layout.item_cell, parent, false)

        val tvCell = view.findViewById<TextView>(R.id.tvCell)
        val ivCellIcon = view.findViewById<ImageView>(R.id.ivCellIcon)
        val cell = getItem(position)

        tvCell.text = ""
        tvCell.setTextColor(context.getColor(android.R.color.black))
        ivCellIcon.visibility = View.GONE

        if (!cell.isRevealed) {
            tvCell.setBackgroundResource(R.drawable.bg_cell_closed)

            if (cell.isFlagged) {
                ivCellIcon.setImageResource(R.drawable.ic_flag)
                ivCellIcon.visibility = View.VISIBLE
            }

            return view
        }

        tvCell.setBackgroundResource(R.drawable.bg_cell_open)

        if (cell.isMine) {
            ivCellIcon.setImageResource(R.drawable.ic_mine)
            ivCellIcon.visibility = View.VISIBLE
            return view
        }

        if (cell.adjacentMines > 0) {
            tvCell.text = cell.adjacentMines.toString()
            tvCell.setTextColor(getNumberColor(cell.adjacentMines))
        }

        return view
    }

    private fun getNumberColor(number: Int): Int {
        return when (number) {
            1 -> context.getColor(R.color.number_1)
            2 -> context.getColor(R.color.number_2)
            3 -> context.getColor(R.color.number_3)
            4 -> context.getColor(R.color.number_4)
            5 -> context.getColor(R.color.number_5)
            6 -> context.getColor(R.color.number_6)
            7 -> context.getColor(R.color.number_7)
            else -> context.getColor(R.color.number_8)
        }
    }
}