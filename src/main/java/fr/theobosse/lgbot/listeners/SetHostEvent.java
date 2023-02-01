package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class SetHostEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("host")) return;
        if (event.getGuild() == null) return;

        List<String> users = event.getValues();
        if (users.size() == 0) return;

        Member m = event.getGuild().retrieveMemberById(users.get(0)).complete();
        if (m == null) return;

        Player player = GamesInfo.getPlayer(m);
        if (player == null) return;

        player.getGame().setHost(player);
        player.getGame().getMessages().updateInvitesMessages();
        event.getMessage().delete().queue();
    }

}
