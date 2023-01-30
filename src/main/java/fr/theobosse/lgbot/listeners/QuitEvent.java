package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.Emotes;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QuitEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("leave")) return;
        if (event.getGuild() == null) return;
        if (event.getMember() == null) return;

        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();
        if (game == null) return;
        if (game.getHost().equals(member)) {
            event.reply("Vous ne pouvez pas quitter cette partie.").setEphemeral(true).queue();
            return;
        }

        game.quit(member);
        event.reply("Vous avez quitté la partie.").setEphemeral(true).queue();
    }

}
