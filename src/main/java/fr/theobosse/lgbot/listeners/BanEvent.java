package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.List;

public class BanEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("ban")) return;
        if (event.getMember() == null) return;
        if (event.getGuild() == null) return;

        List<String> users = event.getValues();
        if (users.size() == 0) return;

        Member m = event.getGuild().getMemberById(users.get(0));
        if (m == null) return;

        Player player = GamesInfo.getPlayer(event.getMember());
        if (player == null) return;

        if (player.getMember().equals(m)) {
            event.reply("Vous ne pouvez pas vous bannir vous-même !").setEphemeral(true).queue();
            return;
        }

        player.getGame().ban(m);
        player.getGame().getMessages().updateOptionsMessages();
        event.getMessage().delete().queue();
    }

}
