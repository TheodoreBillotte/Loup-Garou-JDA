package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

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

        if (GamesInfo.getPlayer(member) != null) {
            event.reply("Vous êtes déjà dans une partie.").setEphemeral(true).queue();
            return;
        }

        if (game.getOptions().gameIsOnInvite())
            game.getOptions().removeInvited(member);

        game.join(member);
        game.getMessages().updateInvitesMessages();
        game.getMessages().updateMainMessage();
        event.reply("Vous avez rejoint la partie, rendez-vous dans le salon d'attente !")
                .setEphemeral(true).queue();
    }

}
