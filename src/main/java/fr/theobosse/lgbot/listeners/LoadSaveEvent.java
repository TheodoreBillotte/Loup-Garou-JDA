package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class LoadSaveEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("load save")) return;
        String value = event.getValues().get(0);
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        LGBot.getLoader().loadData(game, value);
        event.reply("Partie chargée !").setEphemeral(true).queue();

        game.getMessages().updateOptionsMessages();
        game.getMessages().updateRolesMessages();
        game.getMessages().updateWaitingMessages();
        game.getMessages().updateMainMessage();
        game.getChannelsManager().getCreationChannel().getManager().setName(value).queue();
    }
}
