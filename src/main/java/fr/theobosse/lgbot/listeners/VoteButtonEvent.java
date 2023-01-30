package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoteButtonEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("vote")) return;
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (member == null) return;
        if (player == null) return;
        Game game = player.getGame();

        event.reply("Choisissez la personne que vous souhaitez voter")
                .addActionRow(
                        game.getMessages().getPlayerListSelectInteraction(
                                "vote", "Personne à voter"
                        ).build()
                ).setEphemeral(true).queue();
    }

}
