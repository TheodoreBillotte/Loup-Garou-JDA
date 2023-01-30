package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

public class Crow extends GameActions {

    @Override
    public void onPlay(Player player) {
        Member member = player.getMember();
        Game game = player.getGame();
        sendMessage(player);
        ChatManager.setAction(member, "crow");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        Player player = GamesInfo.getPlayer(user);
        String action = ChatManager.getAction(user);

        if (player == null) return;
        if (action == null) return;
        if (!action.equals("crow")) return;
        User toVoteM = null;

        try {
            toVoteM = event.getMessage().getMentions().getUsers().get(0);
        } catch (Exception ignored) {}
        Game game = player.getGame();
        Player toVote;

        if (toVoteM == null) {
            String tag = event.getMessage().getContentDisplay();
            toVote = GamesInfo.getPlayer(player.getGame(), tag);
        } else
            toVote = GamesInfo.getPlayer(toVoteM);

        if (toVote == null) return;
        if (!toVote.isAlive()) return;
        if (!(toVote.getGame() == player.getGame())) return;
        game.getUtils().getVotes().put(toVote, 2);
        ChatManager.removeAction(user);
        user.openPrivateChannel().complete().sendMessageEmbeds(getTargetMessage(toVote).build()).complete();
    }


    private void sendMessage(Player player) {
        User user = player.getMember().getUser();
        PrivateChannel p = user.openPrivateChannel().complete();
        p.sendMessageEmbeds(getMessage().build()).complete();
    }

    private EmbedBuilder getMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est à vous de jouer !");
        eb.addField("Entrez le pseudo de la personne que vous souhaitez voter.",
                "Entrez le pseudo / mentionnez la personne", false);
        eb.setFooter("Faites le bon choix, le village compte sur vous !");
        return eb;
    }

    private EmbedBuilder getTargetMessage(Player target) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous venez de jouer !");
        eb.addField("Vous avez décider de mettre 2 votes sur", target.getMember().getEffectiveName(), false);
        eb.setFooter("Esperons que vous ayez fait le bon choix !");
        return eb;
    }
}
