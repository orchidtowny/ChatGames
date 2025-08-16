package site.remlit.blueb.chatgames

import co.aikar.commands.PaperCommandManager
import site.remlit.blueb.chatgames.command.ChatGamesAdminCommand

class Commands {
    companion object {
        val commandManager = PaperCommandManager(ChatGames.instance)

        fun register() {
            ChatGames.commandManager = commandManager

            commandManager.registerCommand(ChatGamesAdminCommand())
        }
    }
}