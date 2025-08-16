package site.remlit.blueb.chatgames

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class EventListener : Listener {
    @EventHandler
    fun onAsyncChatEvent(e: AsyncChatEvent) {
        if (e.isCancelled) return
        Games.checkAnswer(PlainTextComponentSerializer.plainText().serialize(e.message()), e.player)
    }

    companion object {
        fun register() {
            ChatGames.instance.server.pluginManager.registerEvents(EventListener(), ChatGames.instance)
        }
    }
}