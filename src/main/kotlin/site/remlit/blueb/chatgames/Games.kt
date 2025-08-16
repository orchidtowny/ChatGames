package site.remlit.blueb.chatgames

import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import site.remlit.blueb.chatgames.util.inline.miniMessage
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDateTime
import kotlin.concurrent.thread

class Games {
    companion object {
        var gameRunning = false
        var currentAnswer: String? = null
        var startedAt: LocalDateTime? = null

        fun runGame() {
            val random = /*Random.nextInt(0, 1)*/ 0

            runBlocking {
                gameRunning = true
                when (random) {
                    0 -> runUnscramble()
                }
            }
        }

        fun resetState() {
            gameRunning = false
            currentAnswer = null
            startedAt = null
        }

        fun checkAnswer(attempt: String, player: Player) {
            if (!gameRunning) return

            ChatGames.instance.logger.info("Checking attempt by ${player.name}: $attempt")

            val now = LocalDateTime.now()

            if (
                attempt == currentAnswer &&
                startedAt != null &&
                Duration.between(startedAt!!, now) > Duration.ofMillis(500)
            ) {
                ChatGames.instance.logger.info("Game won by ${player.name}")
                Config.broadcastWin.forEach { line ->
                    ChatGames.instance.server.broadcast(miniMessage {
                        line.replace("{player}", player.name)
                            .replace("{answer}", currentAnswer!!)
                    })
                }
                resetState()
            }
        }

        private fun randomWord() = Config.getFinalWords().random()

        private fun startClock() {
            startedAt = LocalDateTime.now()

            thread(name = "ChatGames Clock Thread") {
                sleep(Config.duration * 1000L)

                if (gameRunning && currentAnswer != null) {
                    Config.broadcastUnscramble.forEach { line ->
                        ChatGames.instance.server.broadcast(miniMessage {
                            line.replace("{answer}", currentAnswer!!)
                        })
                    }
                }

                resetState()
            }
        }

        private fun runUnscramble() {
            val answer = randomWord()
            val scrambled = answer.let {
                fun scramble() = answer.toCharArray().toList().shuffled().joinToString("")
                var attempt = scramble()
                var attempts = 1
                while (attempts < 10 && attempt == answer) { attempt = scramble(); attempts++ }
                attempt
            }

            val time = Config.duration
            ChatGames.instance.logger.info("Running unscramble game for $time sec. Scrambled: $scrambled, Answer: $answer")

            Config.broadcastUnscramble.forEach { line ->
                ChatGames.instance.server.broadcast(miniMessage {
                    line.replace("{sec}", "$time")
                        .replace("{unscramble}", scrambled)
                })
            }

            currentAnswer = answer
            startClock()
        }
    }
}