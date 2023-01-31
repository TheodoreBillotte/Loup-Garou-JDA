package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("role")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        player.getGame().getMessages().sendPlayerRoleMessage(player, event);
    }
}
