package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class VoteEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("vote")) return;
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (member == null) return;
        if (player == null) return;
        Game game = player.getGame();
        if (!game.getUtils().getDay()) return;

        if (game.getUtils().getVoters().get(player) != null) {
            Player old = game.getUtils().getVoters().get(player);
            game.getUtils().getVotes().replace(old, game.getUtils().getVotes().get(old) - 1);
        }

        Player voted = GamesInfo.getPlayer(game, event.getValues().get(0));
        if (voted == null) return;
        game.getUtils().getVoters().put(player, voted);
        game.getUtils().getVotes().putIfAbsent(voted, 0);
        game.getUtils().getVotes().replace(voted, game.getUtils().getVotes().get(voted) + 1);
        event.reply("Vous avez voté pour " + voted.getMember().getAsMention()).setEphemeral(true).queue();
        game.getMessages().updateVotesMessages();
    }

}
