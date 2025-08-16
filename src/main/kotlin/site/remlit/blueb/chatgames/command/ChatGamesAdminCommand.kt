package site.remlit.blueb.chatgames.command

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.CommandPermission
import co.aikar.commands.annotation.Subcommand
import org.bukkit.command.CommandSender
import site.remlit.blueb.chatgames.Config
import site.remlit.blueb.chatgames.Games
import site.remlit.blueb.chatgames.util.inline.miniMessage

@CommandAlias("cga")
@CommandPermission("chatgames.admin")
class ChatGamesAdminCommand : BaseCommand() {
    @Subcommand("reload")
    fun reload(sender: CommandSender) {
        Config.load()
        sender.sendMessage { miniMessage { "<dark_green>ChatGames config and words reloaded successfully!" } }
    }

    @Subcommand("run")
    fun run(sender: CommandSender) {
        if (!Games.gameRunning) {
            Games.runGame()
            sender.sendMessage { miniMessage { "<dark_green>Started random game" } }
        } else {
            sender.sendMessage { miniMessage { "<red>Game already running!" } }
        }
    }
}