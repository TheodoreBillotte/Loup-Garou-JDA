package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Emotes;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Witch extends GameActions {

    private final HashMap<Player, Boolean> revive = new HashMap<>();
    private final HashMap<Player, Boolean> death = new HashMap<>();
    private final HashMap<Player, Message> messages = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        this.revive.putIfAbsent(player, true);
        this.death.putIfAbsent(player, true);
        sendMessage(player);
    }

    @Override
    public void onEndRound(Player player) {
        Game game = player.getGame();
        messages.remove(player);
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        User user = event.getUser();
        String msgId = event.getMessageId();
        Player player = GamesInfo.getPlayer(user);
        Message msg = messages.get(player);
        if (user == null) return;
        if (user.isBot()) return;
        if (player == null) return;

        Emoji e = event.getReaction().getEmoji();
        if (msg == null) return;
        if (!player.getRole().getRound().equals(Rounds.WITCH)) return;
        if (!msgId.equals(msg.getId())) return;

        String action = ChatManager.getAction(user);
        if (e.equals(Emotes.getEmote("bluepotion"))) {
            if (action == null || !action.equals("revive"))
                ChatManager.setAction(user, "revive");
            else
                ChatManager.removeAction(user);
        } else if (e.equals(Emotes.getEmote("orangepotion"))) {
            if (action == null || !action.equals("death"))
                ChatManager.setAction(user, "death");
            else
                ChatManager.removeAction(user);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;
        PrivateChannel channel = event.getChannel().asPrivateChannel();
        Player player = GamesInfo.getPlayer(user);
        String action = ChatManager.getAction(user);

        if (player == null) return;
        Game game = player.getGame();

        if (action == null || !(action.equalsIgnoreCase("revive") || action.equalsIgnoreCase("death"))) return;
        Player toVote = GamesInfo.getPlayer(event.getMessage(), player.getGame());

        if (toVote != null) {
            if (!toVote.isAlive()) return;
            if (!(toVote.getGame() == player.getGame())) return;
            if (action.equalsIgnoreCase("revive") && game.getUtils().getKills().contains(toVote)) {
                sendActionMessage(player, toVote, "ressuciter");
                game.getUtils().getKills().remove(toVote);
                ChatManager.removeAction(user);
            } else if (action.equalsIgnoreCase("death") && !game.getUtils().getKills().contains(toVote)) {
                if (toVote == player) {
                    Messages.sendErrorMessage(channel, "Vous ne pouvez pas vous suicider comme ça !", 30D);
                } else {
                    sendActionMessage(player, toVote, "tuer");
                    game.getUtils().getKills().add(toVote);
                    ChatManager.removeAction(user);
                }
            }
        } else Messages.sendErrorMessage(channel, "La personne visée n'est pas correcte !", 5D);
    }



    public EmbedBuilder getMessage(boolean revive, boolean death, Game game) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("C'est à vous de jouer !");
        eb.setFooter("Faites le bon choix !");

        if (!game.getUtils().getKills().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Player k : game.getUtils().getKills())
                sb.append(k.getMember().getAsMention()).append("   ");
            eb.addField(game.getUtils().getKills().size() == 1 ? "La personne morte cette nuit est : " :
                    "Les personnes mortes cette nuit sont : ", sb.toString(), true);
        } else
            eb.addField("Personne n'a été tué jusqu'à présent !",
                    "Vous ne pouvez donc pas utiliser votre pouvoir de résurection cette nuit !", true);

        eb.addBlankField(false);
        if (revive && !game.getUtils().getKills().isEmpty())
            eb.addField("Vous pouvez ressusciter quelqu'un avec", Emotes.getString(Objects.requireNonNull(Emotes.getEmote("bluepotion"))), false);

        if (death)
            eb.addField("Vous pouvez tuer quelqu'un avec", Emotes.getString(Objects.requireNonNull(Emotes.getEmote("orangepotion"))), false);

        if (!death && !revive)
            eb.addField("Vous avez déjà tout utilisé !", "Vous ne servez plus à rien maintenant !", false);

        return eb;
    }

    public void sendMessage(Player player) {
        Member member = player.getMember();
        User user = member.getUser();

        boolean revive = this.revive.get(player);
        boolean death = this.death.get(player);

        PrivateChannel pc = user.openPrivateChannel().complete();
        Message msg =
                pc.sendMessageEmbeds(getMessage(revive, death, player.getGame()).build()).complete();
        messages.put(player, msg);

        if (revive && !player.getGame().getUtils().getKills().isEmpty())
            msg.addReaction(Objects.requireNonNull(Emotes.getEmote("bluepotion"))).complete();

        if (death)
            msg.addReaction(Objects.requireNonNull(Emotes.getEmote("orangepotion"))).complete();
    }

    public void sendActionMessage(Player player, Player target, String action) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous venez de jouer !");
        eb.setFooter("Esperons que vous avez fait le bon choix !");

        eb.addField("Vous venez de " + action + " :", target.getMember().getEffectiveName(), false);
        player.getMember().getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).complete();
    }
}
