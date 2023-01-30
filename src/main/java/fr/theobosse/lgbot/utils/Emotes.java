package fr.theobosse.lgbot.utils;

import fr.theobosse.lgbot.LGBot;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.emoji.RichCustomEmoji;

import java.util.List;

public class Emotes {

    private static final Guild guild = LGBot.jda.getGuildById(675285855249629244L);


    public static CustomEmoji getEmote(String name) {
        assert guild != null;
        for (CustomEmoji emote : guild.getEmojis()) {
            if (emote.getName().equalsIgnoreCase(name)) {
                return emote;
            }
        }
        return null;
    }

    public static Emoji getEmoteByID(long id) {
        assert guild != null;
        return guild.getEmojiById(id);
    }

    public static Emoji getEmoteByID(String id) {
        assert guild != null;
        return guild.getEmojiById(id);
    }

    public static List<RichCustomEmoji> getEmotes() {
        assert guild != null;
        return guild.getEmojis();
    }

    public static String getString(Emoji emote) {
        return emote.getFormatted();
    }

    public static Guild getGuild() {
        return guild;
    }
}
