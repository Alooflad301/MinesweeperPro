package com.example.minesweeper

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.GridView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class GameActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LEVEL = "extra_level"
    }

    private val viewModel: GameViewModel by viewModels()

    private lateinit var toolbar: MaterialToolbar
    private lateinit var gridView: GridView
    private lateinit var tvMines: TextView
    private lateinit var tvFlags: TextView
    private lateinit var tvTime: TextView
    private lateinit var btnNewGame: MaterialButton
    private lateinit var btnMainMenu: MaterialButton

    private lateinit var cellAdapter: CellAdapter
    private var resultWasShown = false

    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (viewModel.gameState == GameState.PLAYING) {
                viewModel.elapsedSeconds++
                updateInfo()
            }

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        toolbar = findViewById(R.id.toolbar)
        gridView = findViewById(R.id.gridView)
        tvMines = findViewById(R.id.tvMines)
        tvFlags = findViewById(R.id.tvFlags)
        tvTime = findViewById(R.id.tvTime)
        btnNewGame = findViewById(R.id.btnNewGame)
        btnMainMenu = findViewById(R.id.btnMainMenu)

        val levelName = intent.getStringExtra(EXTRA_LEVEL)
            ?: Level.EASY.name

        val selectedLevel = Level.valueOf(levelName)

        if (viewModel.board == null) {
            viewModel.startNewGame(selectedLevel)
        }

        toolbar.title = "Сапёр: ${viewModel.level.title}"

        toolbar.setNavigationOnClickListener {
            returnToMainMenu()
        }

        setupBoard()
        setupClicks()
        setupMouseRightClick()

        updateInfo()
        handler.post(timerRunnable)
    }

    private fun setupBoard() {
        val board = viewModel.board ?: return

        gridView.numColumns = viewModel.level.size
        gridView.isLongClickable = true

        cellAdapter = CellAdapter(this, board)
        gridView.adapter = cellAdapter
    }

    private fun setupClicks() {
        gridView.setOnItemClickListener { _, _, position, _ ->
            viewModel.revealCell(position)
            refreshBoard()
            checkGameResult()
        }

        gridView.setOnItemLongClickListener { _, _, position, _ ->
            viewModel.toggleFlag(position)
            refreshBoard()
            true
        }

        btnNewGame.setOnClickListener {
            viewModel.startNewGame(viewModel.level)
            resultWasShown = false
            setupBoard()
            updateInfo()
        }

        btnMainMenu.setOnClickListener {
            returnToMainMenu()
        }
    }

    private fun setupMouseRightClick() {
        gridView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP &&
                event.buttonState and MotionEvent.BUTTON_SECONDARY != 0
            ) {
                val position = gridView.pointToPosition(
                    event.x.toInt(),
                    event.y.toInt()
                )

                if (position != GridView.INVALID_POSITION) {
                    viewModel.toggleFlag(position)
                    refreshBoard()
                }

                true
            } else {
                view.onTouchEvent(event)
            }
        }
    }

    private fun returnToMainMenu() {
        val intent = Intent(this, MainActivity::class.java)

        intent.flags =
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP

        startActivity(intent)
        finish()
    }

    private fun refreshBoard() {
        val board = viewModel.board ?: return

        cellAdapter.updateBoard(board)
        updateInfo()
    }

    private fun updateInfo() {
        tvMines.text = "Мины: ${viewModel.minesLeft()}"
        tvFlags.text = "Флаги: ${viewModel.flagsUsed()}"
        tvTime.text = "Время: ${viewModel.elapsedSeconds} с"
    }

    private fun checkGameResult() {
        if (resultWasShown) {
            return
        }

        val isWin = viewModel.gameState == GameState.WON
        val isLost = viewModel.gameState == GameState.LOST

        if (isWin || isLost) {
            resultWasShown = true

            ResultDialogFragment.newInstance(
                isWin = isWin,
                timeSeconds = viewModel.elapsedSeconds,
                levelName = viewModel.level.name
            ).show(
                supportFragmentManager,
                ResultDialogFragment.TAG
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(timerRunnable)
    }
}