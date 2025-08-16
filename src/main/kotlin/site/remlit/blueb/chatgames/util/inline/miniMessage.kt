package site.remlit.blueb.chatgames.util.inline

import net.kyori.adventure.text.minimessage.MiniMessage

inline fun miniMessage(block: () -> String) =
    MiniMessage.miniMessage().deserialize(block())