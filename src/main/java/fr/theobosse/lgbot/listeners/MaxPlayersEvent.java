package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Options;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class MaxPlayersEvent extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("maxPlayers")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Options options = player.getGame().getOptions();
        int value = Integer.parseInt(Objects.requireNonNull(event.getValue("maxPlayers")).getAsString());

        if (value < 1 || value > 25) {
            event.reply("Le nombre de joueurs doit être compris entre 1 et 25").setEphemeral(true).queue();
            return;
        }

        options.setMaxPlayers(value);
        player.getGame().getMessages().updateOptionsMessages();
        player.getGame().getMessages().updateInfoMessages();
        event.deferEdit().queue();
    }

}
