package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LittleGirl extends GameActions {
    private final HashMap<Player, List<String>> nightMessages = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        nightMessages.remove(player);
        nightMessages.put(player, new ArrayList<>());
    }

    @Override
    public void onEndRound(Player player) {
        Member member = player.getMember();
        PrivateChannel pc = member.getUser().openPrivateChannel().complete();
        pc.sendMessageEmbeds(getMessage(player).build()).complete();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        String msg = event.getMessage().getContentDisplay();
        if (!event.getChannelType().equals(ChannelType.TEXT))
            return;
        TextChannel channel = event.getChannel().asTextChannel();

        if (player == null) return;
        if (channel != player.getGame().getChannelsManager().getWerewolfChannel()) return;
        if (msg.equals("")) return;

        for (Player pf : getPF(player.getGame()))
            addMessageWW(pf, msg);
    }

    public void addMessageWW(Player player, String message) {
        nightMessages.putIfAbsent(player, new ArrayList<>());
        nightMessages.get(player).add(message);
    }

    public List<Player> getPF(Game game) {
        List<Player> players = new ArrayList<>();
        try {
            game.getUtils().getPlayers().forEach(p -> {
                try {
                    if (p.getRole().getSubName().equals("PF"))
                        players.add(p);
                } catch (Exception ignored) {}
            });
        } catch (Exception ignored) {}
        return players;
    }

    private EmbedBuilder getMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Voici le récapitulatif de cette nuit !");
        for (String msg : nightMessages.get(player))
            eb.addField("```******:```", msg, false);
        return eb;
    }
}
