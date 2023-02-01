package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LittleGirl extends GameActions {
    private final HashMap<Player, List<String>> nightMessages = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        if (!nightMessages.containsKey(player))
            nightMessages.put(player, new ArrayList<>());
        else
            nightMessages.get(player).clear();
    }

    @Override
    public void onEndRound(Player player) {
        player.getGame().getChannelsManager().getVillageChannel().sendMessageEmbeds(getPFMessage().build())
                .addActionRow(
                        Button.primary("PF", "Voir")
                ).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("PF")) return;
        Player player = GamesInfo.getPlayer(event.getMember());
        if (player == null) return;
        if (player.getRole().getSubName().equals("PF"))
            event.replyEmbeds(getMessage(player).build()).setEphemeral(true).queue();
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

    private EmbedBuilder getPFMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Voici le récapitulatif de cette nuit !");
        eb.addField("Si vous êtes la petite fille:", "cliquez sur le bouton ci-dessous", false);
        return eb;
    }

    private EmbedBuilder getMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Voici le récapitulatif de cette nuit !");
        for (String msg : nightMessages.get(player))
            eb.addField("```******:```", msg, false);
        return eb;
    }
}
