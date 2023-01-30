package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class WereWolf extends GameActions {

    private final HashMap<Game, HashMap<Player, Player>> votes = new HashMap<>();
    private final HashMap<Game, Message> messages = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        Game game = player.getGame();

        if (!messages.containsKey(game)) {
            sendAlertMessage(player);
            sendMessage(player);
        }
    }

    @Override
    public void onEndRound(Player player) {
        Game game = player.getGame();
        Random random = new Random();
        long count = 0;
        Player target = null;
        for (Player p : votes.get(game).values()) {
            long nb = getVotes(p);
            if (nb > count) {
                count = nb;
                target = p;
            } else if (nb == count)
                if (random.nextInt(2) == 0)
                    target = p;
        }

        if (target != null)
            game.getUtils().addKills(target);

        votes.remove(game);
        messages.remove(game);
    }



    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Member member = event.getMember();
        String msgId = event.getMessageId();
        MessageChannel channel = event.getChannel();
        Player player = GamesInfo.getPlayer(member);
        if (member.getUser().isBot()) return;
        if (player == null) return;

        Emoji e = event.getReaction().getEmoji();
        Game game = player.getGame();

        if (messages.get(game) == null) return;
        if (game.getChannelsManager().getWerewolfChannel() == null) return;
        if (!channel.getId().equals(game.getChannelsManager().getWerewolfChannel().getId())) return;
        if (!player.getRole().getRound().equals(Rounds.WEREWOLF)) return;
        if (!msgId.equals(messages.get(game).getId())) return;

        if (e.equals(Emotes.getEmote("werewolf"))) {
            String action = ChatManager.getAction(member);
            messages.get(game).removeReaction(Objects.requireNonNull(Emotes.getEmote("werewolf")), member.getUser()).complete();
            if (action == null || !action.equals("lgVote"))
                ChatManager.setAction(member, "lgVote");
            else
                ChatManager.removeAction(member);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT))
            return;
        TextChannel channel = event.getChannel().asTextChannel();
        Member member = event.getMember();

        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        String action = ChatManager.getAction(member);

        if (player == null) return;
        if (action == null || !action.equalsIgnoreCase("lgVote")) return;
        if (!channel.equals(player.getGame().getChannelsManager().getWerewolfChannel())) return;
        Player p = GamesInfo.getPlayer(event.getMessage(), player.getGame());

        if (p == null) return;
        if (p.getGame() != player.getGame()) return;

        vote(player, p);
        updateMessage(player);
        event.getMessage().delete().complete();
    }



    public EmbedBuilder getMessage(Player player) {
        Game game = player.getGame();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est l'heure de voter !!");
        eb.setFooter("N'oubliez pas de voter pour une cible !");
        eb.addField("Voilà les votes:", "Si vous n'avez pas voté, n'hésitez pas !", false);
        votes.putIfAbsent(game, new HashMap<>());

        game.getUtils().getAlive().forEach(p -> {
            try {
                if (p.getRole().getClan().equals(Clan.WEREWOLF))
                    eb.addField(
                            p.getMember().getEffectiveName(),
                            votes.get(game).containsKey(p) ? votes.get(game).get(p).getMember().getAsMention() : "PERSONNE",
                            true);
            } catch (Exception ignored) {}
        });

        eb.addField("Pour voter:", Emotes.getString(Objects.requireNonNull(Emotes.getEmote("werewolf"))),
                false);
        return eb;
    }

    public EmbedBuilder getAlertMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est le tour des Loup-Garou !");
        eb.setFooter("Faites attention 😉");
        return eb;
    }



    public void sendMessage(Player player) {
        Game game = player.getGame();

        Message msg =
                game.getChannelsManager().getWerewolfChannel().sendMessageEmbeds(getMessage(player).build()).complete();
        msg.addReaction(Objects.requireNonNull(Emotes.getEmote("werewolf"))).complete();
        messages.put(game, msg);
    }

    public void sendAlertMessage(Player player) {
        Game game = player.getGame();
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getAlertMessage().build()).complete();
    }

    public void updateMessage(Player player) {
        Game game = player.getGame();

        try {
            messages.get(game).editMessageEmbeds(getMessage(player).build()).complete();
        } catch (Exception ignored) {}
    }



    public void vote(Player player, Player voted) {
        Game game = player.getGame();

        if (votes.containsKey(game)) {
            votes.get(game).put(player, voted);
        } else {
            HashMap<Player, Player> vote = new HashMap<>();
            vote.put(player, voted);
            votes.put(game, vote);
        }
    }

    public long getVotes(Player voted) {
        return votes.get(voted.getGame()).values().stream().filter(voted::equals).count();
    }
}
