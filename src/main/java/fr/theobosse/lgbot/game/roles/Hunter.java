package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.*;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.utils.Emotes;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;

public class Hunter extends Role {

    ArrayList<Player> shooting = new ArrayList<>();

    public Hunter() {
        setName("Chasseur");
        setSubName("Chasseur");
        setClan(Clan.VILLAGE);
        setEmoji(Emotes.getEmote("hunter"));
        setRound(null);

        setDescription("Son objectif est d'éliminer tous les Loups-Garous. A sa mort, il doit éliminer un joueur" +
                " en utilisant sa dernière balle...");
    }

    @Override
    public void onNightDeath(Player player) {
        GameUtils utils = player.getGame().getUtils();
        utils.setTime(Long.MAX_VALUE);
        sendDeathMessage(player);
    }

    @Override
    public void onVoteDeath(Player player) {
        GameUtils utils = player.getGame().getUtils();
        utils.setTime(Long.MAX_VALUE);
        sendDeathMessage(player);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("kill")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        if (!shooting.contains(player)) return;
        Game game = player.getGame();
        if (player.getRole().getName().equals("Chasseur")) {
            event.reply("Choisissez la personne à tuer !")
                    .addActionRow(
                            Messages.getPlayerListSelectInteraction(game.getUtils().getAlive(), "hunt", "Cible").build()
                    ).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("hunt")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();
        if (!shooting.contains(player)) return;
        if (player.getRole().getName().equals("Chasseur")) {
            Player target = GamesInfo.getPlayer(game, event.getValues().get(0));
            if (target == null) return;
            game.kill(target);
            game.getUtils().setTime(0L);
            event.replyEmbeds(getKillMessage(target).build()).queue();
            shooting.remove(player);
        }
    }



    private void sendDeathMessage(Player player) {
        shooting.add(player);
        Game game = player.getGame();
        TextChannel village = game.getChannelsManager().getVillageChannel();
        village.sendMessageEmbeds(getDeathMessage().build())
                .addActionRow(
                        Button.success("kill", "Tuer !")
                ).queue();
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
        eb.addField("Pour tuer, cliquez sur le bouton ci-dessous et entrez le pseudo de votre cible !",
                "Faites le bon choix !", false);
        eb.setFooter("Choisissez bien votre cible !");
        return eb;
    }
}
