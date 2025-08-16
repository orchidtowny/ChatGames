package site.remlit.blueb.chatgames

import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.exists

class Config {
    companion object {
        var interval: Int = 30 // min
        var duration: Int = 30 // sec
        var includePlayerNames: Boolean = true
        var rewards: List<String> = emptyList<String>()

        var broadcastUnscramble: List<String> = listOf(
            "<white>",
            "<yellow>You have {sec} sec to unscramble: <blue>{unscramble}",
            "<white>"
        )
        var broadcastWin: List<String> = listOf(
            "<white>",
            "<green>{player} won in {sec} sec! The answer was {unscramble}",
            "<white>"
        )
        var broadcastFail: List<String> = listOf(
            "<white>",
            "<red>Nobody got it! The answer was {unscramble}",
            "<white>"
        )

        var words: MutableList<String> = mutableListOf<String>()

        fun getFinalWords(): List<String> {
            val finalWords = words

            if (includePlayerNames)
                ChatGames.instance.server.onlinePlayers.forEach { player ->
                    finalWords.add(player.name)
                }

            return finalWords
        }


        private val wordsPath = Path(ChatGames.instance.dataFolder.toString(), "words.txt")

        fun init() {
            val config = ChatGames.instance.config

            config.addDefault("interval", 30)
            config.addDefault("duration", 30)
            config.addDefault("include-player-names", true)
            config.addDefault("rewards", listOf("25;points give {player} 1"))

            config.addDefault("broadcast.unscramble", listOf(
                "<white>",
                "<yellow>You have {sec} sec to unscramble: <blue>{unscramble}",
                "<white>"
            ))
            config.addDefault("broadcast.win", listOf(
                "<white>",
                "<green>{player} won in {sec} sec! The answer was {answer}",
                "<white>"
            ))
            config.addDefault("broadcast.fail", listOf(
                "<white>",
                "<red>Nobody got it! The answer was {answer}",
                "<white>"
            ))

            config.options().copyDefaults(true)

            ChatGames.instance.saveConfig()

            try {
                if (!wordsPath.exists()) Files.createFile(wordsPath)
            } catch (e: Exception) {
                ChatGames.instance.logger.severe("Failed to create words.txt")
            }
        }

        fun load() {
            val config = ChatGames.instance.config

            interval = config.getInt("interval")
            duration = config.getInt("duration")
            includePlayerNames = config.getBoolean("include-player-names")
            rewards = config.getStringList("rewards")
            broadcastUnscramble = config.getStringList("broadcast.unscramble")
            broadcastWin = config.getStringList("broadcast.win")
            broadcastFail = config.getStringList("broadcast.fail")

            try {
                words.clear()
                Files.lines(wordsPath).forEach { line ->
                    words.add(line)
                }
            } catch (e: Exception) {
                ChatGames.instance.logger.severe("Failed to read words.txt")
            }
        }
    }
}