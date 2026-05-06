package com.ud.riddle.repositories

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.ud.riddle.models.Game
import com.ud.riddle.models.enums.GameStateEnum

class FirebaseGameDataSource : GameDataSource {
    private val database = Firebase.database
    private val gameRef = database.getReference("games")

    override fun createGame(playerId: String, word: String, clue: String, onComplete: (String) -> Unit) {
        val code = gameRef.push().key ?: return
        val game = Game(
            code = code,
            player1 = playerId,
            status = GameStateEnum.CREATING_PLAYERS,
            turnPlayerId = playerId,
            word = word,
            clue = clue
        )
        gameRef.child(code).setValue(game).addOnSuccessListener {
            onComplete(code)
        }
    }

    override fun joinGame(code: String, playerId: String, onComplete: () -> Unit) {
        gameRef.child(code).updateChildren(
            mapOf("player2" to playerId)
        ).addOnSuccessListener { onComplete() }
    }

    override fun listenGame(code: String, onUpdate: (Game?) -> Unit) {
        gameRef.child(code).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                onUpdate(snapshot.getValue(Game::class.java))
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun removeListener() {
        // Implementación del removelistener si es necesario
    }
}
