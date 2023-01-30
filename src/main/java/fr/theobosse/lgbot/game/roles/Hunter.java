package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.*;
import fr.theobosse.lgbot.utils.ChatManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Hunter extends GameActions {

    @Override
    public void onNightDeath(Player player) {
        GameUtils utils = player.getGame().getUtils();
        utils.setTime(Long.MAX_VALUE);
        sendDeathMessage(player);
        ChatManager.setAction(player.getMember(), "hunt");
    }

    @Override
    public void onVoteDeath(Player player) {
        GameUtils utils = player.getGame().getUtils();
        utils.setTime(Long.MAX_VALUE);
        sendDeathMessage(player);
        ChatManager.setAction(player.getMember(), "hunt");
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member member = event.getMember();
        assert member != null;
        Message message = event.getMessage();
        Player player = GamesInfo.getPlayer(member);
        String action = ChatManager.getAction(member);
        if (player == null) return;
        Game game = player.getGame();
        Member target;
        try {
            target = message.getMentions().getMembers().get(0);
        } catch (Exception ignored) {
            try {
                target = Objects.requireNonNull(GamesInfo.getPlayer(game, message.getContentDisplay())).getMember();
            } catch (Exception i) {
                target = null;
            }
        }
        if (target == null) return;
        if (action == null || !action.equals("hunt")) return;
        Player t = GamesInfo.getPlayer(target);
        ChatManager.removeAction(member);
        assert t != null;
        game.kill(t);
        sendKillMessage(t);
        game.getUtils().setTime(0L);
    }



    private void sendKillMessage(Player target) {
        Game game = target.getGame();
        TextChannel village = game.getChannelsManager().getVillageChannel();
        village.sendMessageEmbeds(getKillMessage(target).build()).complete();
    }

    private void sendDeathMessage(Player player) {
        Game game = player.getGame();
        TextChannel village = game.getChannelsManager().getVillageChannel();
        village.sendMessageEmbeds(getDeathMessage().build()).complete();
    }



    private EmbedBuilder getKillMessage(Player target) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Le chasseur vient de se venger !");
        eb.addField("Le chasseur a décidé de tuer", target.getMember().getEffectiveName(), false);
        eb.setFooter("Esperons qu'il ai fait le bon choix !");
        return eb;
    }

    private EmbedBuilder getDeathMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Le chasseur est mort cette nuit !");
        eb.addField("Pour tuer, entrez le pseudo de la personne ciblé !", "Faites le bon choix !", false);
        eb.setFooter("Choisissez bien votre cible !");
        return eb;
    }
}
