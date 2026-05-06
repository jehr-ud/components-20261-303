package com.ud.riddle.repositories

import com.ud.riddle.models.Game
import com.ud.riddle.models.enums.GameStateEnum

class LocalGameDataSource : GameDataSource {

    private var currentGame: Game? = null

    override fun createGame(playerId: String, word: String, clue: String, onComplete: (String) -> Unit) {
        val game = Game(
            code = "LOCAL",
            player1 = playerId,
            status = GameStateEnum.CREATING_PLAYERS,
            turnPlayerId = playerId,
            word = word,
            clue = clue
        )
        currentGame = game
        onComplete("LOCAL")
    }

    override fun joinGame(code: String, playerId: String, onComplete: () -> Unit) {
        currentGame = currentGame?.copy(player2 = playerId)
        onComplete()
    }

    override fun listenGame(code: String, onUpdate: (Game?) -> Unit) {
        onUpdate(currentGame)
    }

    override fun removeListener() {
    }
}
