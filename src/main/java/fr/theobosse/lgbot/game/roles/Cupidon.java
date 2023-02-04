package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.*;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.Emotes;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cupidon extends Role {

    ArrayList<Player> playing = new ArrayList<>();

    public Cupidon() {
        setName("Cupidon");
        setSubName("Cupidon");
        setClan(Clan.VILLAGE);
        setEmoji(Emotes.getEmote("cupid"));
        setRound(Rounds.CUPIDON);

        setDescription("Son objectif est d'éliminer tous les Loups-Garous. Dès le début de la partie, il doit former" +
                " un couple de deux joueurs. Leur objectif sera de survivre ensemble, car si l'un d'eux meurt," +
                " l'autre se suicidera.");
    }

    @Override
    public void onPlay(Player player) {
        if (player.getGame().getUtils().getCouple(player) == null) {
            playing.add(player);
            sendPlayMessage(player);
        } else
            player.getGame().getGameRunning().played();
    }

    @Override
    public void onEndRound(Player player) {
        playing.remove(player);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().startsWith("cupidon")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        if (event.getComponentId().equals("cupidon play")) {
            if (playing.contains(player)) {
                event.replyEmbeds(getCupidMessage().build())
                        .addActionRow(
                                Messages.getPlayerListSelectInteraction(game.getUtils().getAlive(),
                                        "cupidon", "Vos cibles").setRequiredRange(2, 2).build()
                        ).setEphemeral(true).queue();
            }
        } else {
            String name = event.getComponentId().replace("cupidon ", "");
            Player p = GamesInfo.getPlayer(name);
            if (p == null) return;
            List<Player> couple = game.getUtils().getCouple(p);
            if (couple != null && couple.contains(player))
                event.replyEmbeds(getSeeCoupleMessage(p).build()).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("cupidon")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();
        String target1 = event.getValues().get(0);
        String target2 = event.getValues().get(1);

        if (playing.contains(player)) {
            Player p1 = GamesInfo.getPlayer(target1);
            Player p2 = GamesInfo.getPlayer(target2);
            if (p1 == null || p2 == null) return;
            game.getUtils().getCouples().put(player, Arrays.asList(p1, p2));
            event.reply("Vous avez choisi " + p1.getMember().getEffectiveName() + " et " +
                            p2.getMember().getEffectiveName() + " pour être amoureux !").setEphemeral(true).queue();
            sendCoupleMessage(player);
            player.getGame().getGameRunning().played();
            playing.remove(player);
        }
    }

    private void sendPlayMessage(Player player) {
        player.getGame().getChannelsManager().getVillageChannel().sendMessageEmbeds(getPlayMessage().build())
                .addActionRow(
                        Button.success("cupidon play", "Jouer")
                ).queue();
    }

    private void sendCoupleMessage(Player player) {
        player.getGame().getChannelsManager().getVillageChannel().sendMessageEmbeds(getCoupleMessage().build())
                .addActionRow(
                        Button.success("cupidon " + player.getMember().getEffectiveName(), "Voir le couple")
                ).queue();
    }

    private EmbedBuilder getCupidMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("C'est à vous de jouer !");
        builder.setDescription("Choisissez ci-dessous deux personnes que vous souhaitez mettre en couple !");
        builder.setFooter("Vous pourrez gagner avec le couple !");
        return builder;
    }

    private EmbedBuilder getPlayMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("C'est au tour du cupidon");
        builder.setDescription("il peut choisir deux personnes pour qu'elles soient amoureuses");
        builder.setFooter("Il pourra gagner avec le couple !");
        return builder;
    }

    private EmbedBuilder getCoupleMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Le couple a été choisi !");
        builder.setDescription("Pour savoir si vous êtes en couple cliquez sur le bouton ci-dessous !");
        builder.setColor(Color.PINK);
        return builder;
    }

    private EmbedBuilder getSeeCoupleMessage(Player player) {
        EmbedBuilder builder = new EmbedBuilder();
        List<Player> couple = player.getGame().getUtils().getCouple(player);
        builder.setTitle("Voici le couple pour cette partie:");
        builder.setDescription(couple.get(0).getMember().getEffectiveName() + " et " +
                couple.get(1).getMember().getEffectiveName());
        builder.setColor(Color.PINK);
        return builder;
    }
}
