package site.remlit.blueb.chatgames

import co.aikar.commands.PaperCommandManager
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class ChatGames : JavaPlugin() {

    override fun onEnable() {
        instance = this

        Config.init()
        Config.load()
        Commands.register()
        EventListener.register()

        thread(name = "ChatGames Background Thread") {
            sleep(Config.interval * 60 * 1000L)
            Games.runGame()
        }
    }

    override fun onDisable() {
    }

    companion object {
        lateinit var instance: ChatGames
        lateinit var commandManager: PaperCommandManager
    }
}
