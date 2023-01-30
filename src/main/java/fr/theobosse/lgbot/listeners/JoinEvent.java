package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Options;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class JoinEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("join")) return;
        if (event.getGuild() == null) return;
        if (event.getMember() == null) return;

        Member member = event.getMember();
        Game game = GamesInfo.getGame(event.getMessageId());
        if (game == null) return;
        if (!game.canJoin(member)) {
            event.reply("Vous ne pouvez pas rejoindre cette partie.").setEphemeral(true).queue();
            return;
        }

        if (game.getOptions().gameIsOnInvite())
            game.getOptions().removeInvited(member);

        game.join(member);
        game.getMessages().updateInvitesMessages();
        game.getMessages().updateMainMessage();
    }

}
