package com.example.minesweeper

class Board(
    val size: Int,
    val mineCount: Int
) {
    val cells = Array(size * size) { Cell() }

    fun generateMines(safeIndex: Int) {
        val excludedIndexes = mutableSetOf(safeIndex)
        excludedIndexes.addAll(getNeighbors(safeIndex))

        val availableIndexes = cells.indices
            .filter { it !in excludedIndexes }
            .shuffled()

        availableIndexes
            .take(mineCount)
            .forEach { index ->
                cells[index].isMine = true
            }

        cells.indices.forEach { index ->
            if (!cells[index].isMine) {
                cells[index].adjacentMines =
                    getNeighbors(index).count { neighborIndex ->
                        cells[neighborIndex].isMine
                    }
            }
        }
    }

    fun getNeighbors(index: Int): List<Int> {
        val row = index / size
        val column = index % size
        val neighbors = mutableListOf<Int>()

        for (rowOffset in -1..1) {
            for (columnOffset in -1..1) {
                if (rowOffset == 0 && columnOffset == 0) {
                    continue
                }

                val neighborRow = row + rowOffset
                val neighborColumn = column + columnOffset

                if (neighborRow in 0 until size &&
                    neighborColumn in 0 until size
                ) {
                    neighbors.add(neighborRow * size + neighborColumn)
                }
            }
        }

        return neighbors
    }
}