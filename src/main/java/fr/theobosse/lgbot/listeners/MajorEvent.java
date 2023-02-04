package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MajorEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("major")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();
        if (game.getUtils().getMajor().isAlive()) return;
        if (game.getUtils().getMajor() != player) return;

        Player voted = GamesInfo.getPlayer(game, event.getValues().get(0));
        if (voted == null) return;

        event.deferEdit().queue();
        game.getUtils().setMajor(voted);
        game.getMessages().sendElectMessage();
        game.getUtils().setTime(0L);
    }
}
