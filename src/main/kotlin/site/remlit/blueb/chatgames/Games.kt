package site.remlit.blueb.chatgames

import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import site.remlit.blueb.chatgames.util.inline.miniMessage
import java.lang.Thread.sleep
import java.time.Duration
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.random.Random

class Games {
    companion object {
        var gameRunning = false
        var currentAnswer: String? = null
        var startedAt: LocalDateTime? = null

        fun runGame() {
            val random = Random.nextInt(0, 100)

            runBlocking {
                gameRunning = true

                if (random >= 50) runType()
                else runUnscramble()
            }
        }

        fun resetState() {
            gameRunning = false
            currentAnswer = null
            startedAt = null
        }

        fun checkAnswer(attempt: String, player: Player): Boolean {
            if (!gameRunning) return false

            val now = LocalDateTime.now()
            val duration = Duration.between(startedAt!!, now)

            if (
                attempt == currentAnswer &&
                startedAt != null &&
                duration > Duration.ofMillis(500)
            ) {
                Config.broadcastWin.forEach { line ->
                    ChatGames.instance.server.broadcast(miniMessage {
                        line.replace("{player}", player.name)
                            .replace("{sec}", "${duration.seconds}")
                            .replace("{answer}", currentAnswer!!)
                    })
                }

                val rewardCommand = Config.rewards.random().replace("{player}", player.name)
                if (!rewardCommand.isBlank())
                    Bukkit.getScheduler().runTask(ChatGames.instance, Runnable {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), rewardCommand)
                    })

                resetState()
                return true
            }

            return false
        }

        private fun randomWord() = Config.getFinalWords().random()

        private fun startClock() {
            startedAt = LocalDateTime.now()

            thread(name = "ChatGames Clock Thread") {
                sleep(Config.duration * 1000L)

                if (gameRunning && currentAnswer != null) {
                    Config.broadcastFail.forEach { line ->
                        ChatGames.instance.server.broadcast(miniMessage {
                            line.replace("{answer}", currentAnswer!!)
                        })
                    }
                }

                resetState()
            }
        }

        /* -- Games -- */

        private fun runType() {
            val answer = randomWord()

            val time = Config.duration
            ChatGames.instance.logger.info("Running type game for $time sec. Answer: $answer")

            Config.broadcastType.forEach { line ->
                ChatGames.instance.server.broadcast(miniMessage {
                    line.replace("{sec}", "$time")
                        .replace("{word}", answer)
                })
            }

            currentAnswer = answer
            startClock()
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