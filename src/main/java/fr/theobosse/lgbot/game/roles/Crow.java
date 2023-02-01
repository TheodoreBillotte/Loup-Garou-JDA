package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Crow extends GameActions {

    @Override
    public void onPlay(Player player) {
        sendMessage(player);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("crow")) return;
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        if (!player.isAlive()) return;
        Game game = player.getGame();
        if (!game.getUtils().getRound().equals(Rounds.CROW)) return;
        if (!player.getRole().getName().equals("Corbeau")) return;
        event.reply("Choisissez la personne que vous souhaitez voter.")
                .addActionRow(
                        Messages.getPlayerListSelectInteraction(game.getUtils().getAlive(), "crow", "Cible").build()
                ).setEphemeral(true).queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("crow")) return;
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        if (!player.isAlive()) return;
        Game game = player.getGame();
        if (game.getUtils().getRound() == null || !game.getUtils().getRound().equals(Rounds.CROW)) return;
        if (!player.getRole().getName().equals("Corbeau")) return;
        String value = event.getValues().get(0);

        Player toVote = GamesInfo.getPlayer(player.getGame(), value);
        if (toVote == null) return;
        if (!toVote.isAlive()) return;
        game.getUtils().getVotes().put(toVote, 2);
        event.replyEmbeds(getTargetMessage(toVote).build())
                .setEphemeral(true).queue();
        game.getGameRunning().played();
    }

    private void sendMessage(Player player) {
        TextChannel village = player.getGame().getChannelsManager().getVillageChannel();
        village.sendMessageEmbeds(getMessage().build()).addActionRow(
                Button.success("crow", "Voter")
        ).queue();
    }

    private EmbedBuilder getMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est au corbeau de jouer !");
        eb.addField("Cliquez sur le bouton ci-dessous et entrez le pseudo de votre cible !",
                "Selectionnez votre cible dans le menu déroulant", false);
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
