package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class Cupidon extends GameActions {

    ArrayList<Player> playing = new ArrayList<>();

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
        if (!event.getComponentId().equals("cupidon play")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        if (playing.contains(player)) {
            event.replyEmbeds(getCupidMessage().build())
                    .addActionRow(
                            Messages.getPlayerListSelectInteraction(game.getUtils().getAlive(),
                                    "cupidon","Vos cibles").setRequiredRange(2, 2).build()
                    ).queue();
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
}
