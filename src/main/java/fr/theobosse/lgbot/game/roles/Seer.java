package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Emotes;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class Seer extends GameActions {

    private final HashMap<Player, Boolean> power = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        ChatManager.setAction(player.getMember(), "seer");
        power.put(player, true);
        sendMessage(player);
    }

    @Override
    public void onEndRound(Player player) {
        ChatManager.removeAction(player.getMember());
        power.remove(player);
    }

    /*
    @Override
    public void onPrivateMessageReactionAdd(@NotNull PrivateMessageReactionAddEvent event) {
        User user = event.getUser();
        Player player = GamesInfo.getPlayer(user);
        if (player == null) return;
        if (!canUsePower(player)) return;
        if (ChatManager.getAction(user) == null || !ChatManager.getAction(user).equals("seer"))
            ChatManager.setAction(user, "seer");
        else ChatManager.removeAction(user);
    }
    */

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        Player player = GamesInfo.getPlayer(user);
        String action = ChatManager.getAction(user);
        if (!event.getChannelType().equals(ChannelType.PRIVATE))
            return;
        PrivateChannel channel = event.getChannel().asPrivateChannel();
        if (player == null) return;
        if (!player.isAlive()) return;
        if (action == null) return;
        if (!action.equals("seer")) return;
        Player p = GamesInfo.getPlayer(event.getMessage(), player.getGame());

        if (p == player) {
            Messages.sendErrorMessage(channel, "Vous ne pouvez pas regarder votre role !", 5D);
            return;
        }

        if (p == null) return;
        if (!p.isAlive()) return;
        if (!(p.getGame() == player.getGame())) return;
        sendRoleMessage(player, p);
        ChatManager.removeAction(user);
        usePower(player);
    }

    private void usePower(Player player) {
        power.put(player, false);
    }

    private boolean canUsePower(Player player) {
        return power.get(player) != null && power.get(player);
    }

    private EmbedBuilder getMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est à vous de jouer !");
        eb.setFooter("Faites le bon choix !");
        /* eb.addField("Utilisez votre pouvoir en cliquant sur", Emotes.getString(Emotes.getEmote("seer")), false); */
        return eb;
    }

    private void sendMessage(Player player) {
        player.getMember().getUser().openPrivateChannel().complete().
                sendMessageEmbeds(getMessage().build()).complete().
                addReaction(Objects.requireNonNull(Emotes.getEmote("seer"))).complete();
    }

    private EmbedBuilder getRoleMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous venez d'utiliser votre pouvoir !");
        eb.addField("Le role de " + player.getMember().getEffectiveName() + " est :", player.getRole().getName(), false);
        eb.setFooter("Cela met donc fin à vos soupçons sur cette personne !");
        return eb;
    }

    private void sendRoleMessage(Player player, Player target) {
        player.getMember().getUser().openPrivateChannel().complete().
                sendMessageEmbeds(getRoleMessage(target).build()).complete();
    }
}
