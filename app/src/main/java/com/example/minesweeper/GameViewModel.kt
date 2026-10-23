package com.example.minesweeper

import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    var level: Level = Level.EASY
        private set

    var board: Board? = null
        private set

    var gameState: GameState = GameState.NOT_STARTED
        private set

    var elapsedSeconds: Int = 0

    fun startNewGame(selectedLevel: Level) {
        level = selectedLevel
        board = Board(
            size = selectedLevel.size,
            mineCount = selectedLevel.mineCount
        )
        gameState = GameState.NOT_STARTED
        elapsedSeconds = 0
    }

    fun revealCell(index: Int) {
        val currentBoard = board ?: return

        if (gameState == GameState.WON ||
            gameState == GameState.LOST
        ) {
            return
        }

        val cell = currentBoard.cells[index]

        if (cell.isRevealed || cell.isFlagged) {
            return
        }

        if (gameState == GameState.NOT_STARTED) {
            currentBoard.generateMines(index)
            gameState = GameState.PLAYING
        }

        if (cell.isMine) {
            cell.isRevealed = true
            revealAllMines()
            gameState = GameState.LOST
            return
        }

        revealEmptyArea(index)
        checkWin()
    }

    fun toggleFlag(index: Int) {
        val currentBoard = board ?: return

        if (gameState == GameState.WON ||
            gameState == GameState.LOST
        ) {
            return
        }

        val cell = currentBoard.cells[index]

        if (!cell.isRevealed) {
            cell.isFlagged = !cell.isFlagged
        }
    }

    private fun revealEmptyArea(index: Int) {
        val currentBoard = board ?: return
        val cell = currentBoard.cells[index]

        if (cell.isRevealed ||
            cell.isFlagged ||
            cell.isMine
        ) {
            return
        }

        cell.isRevealed = true

        if (cell.adjacentMines == 0) {
            currentBoard.getNeighbors(index).forEach { neighborIndex ->
                revealEmptyArea(neighborIndex)
            }
        }
    }

    private fun revealAllMines() {
        board?.cells?.forEach { cell ->
            if (cell.isMine) {
                cell.isRevealed = true
            }
        }
    }

    private fun checkWin() {
        val currentBoard = board ?: return

        val safeCellsCount = currentBoard.cells.count {
            !it.isMine
        }

        val openedSafeCellsCount = currentBoard.cells.count {
            it.isRevealed && !it.isMine
        }

        if (openedSafeCellsCount == safeCellsCount) {
            gameState = GameState.WON
        }
    }

    fun flagsUsed(): Int {
        return board?.cells?.count { it.isFlagged } ?: 0
    }

    fun minesLeft(): Int {
        return level.mineCount - flagsUsed()
    }
}