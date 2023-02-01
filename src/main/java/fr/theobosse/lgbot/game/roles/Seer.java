package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;

public class Seer extends GameActions {

    private final HashMap<Player, Boolean> power = new HashMap<>();

    @Override
    public void onPlay(Player player) {
        power.put(player, true);
        sendMessage(player);
    }

    @Override
    public void onEndRound(Player player) {
        power.remove(player);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("seer")) return;
        Player player = GamesInfo.getPlayer(event.getMember());
        if (player == null) return;
        if (!canUsePower(player)) return;

        event.reply("Choisissez une personne à voir")
                .addActionRow(
                        player.getGame().getMessages().getPlayerListSelectInteraction("seer", "Cible").build()
                ).setEphemeral(true).queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("seer")) return;
        Player player = GamesInfo.getPlayer(event.getMember());
        if (player == null) return;
        if (!canUsePower(player)) return;
        Player target = GamesInfo.getPlayer(player.getGame(), event.getValues().get(0));
        if (target == null) return;
        if (!target.isAlive()) return;
        if (!(target.getGame() == player.getGame())) return;
        if (target == player) {
            event.reply("Vous ne pouvez pas regarder votre role !").setEphemeral(true).queue();
            return;
        }
        event.replyEmbeds(getRoleMessage(target).build()).setEphemeral(true).queue();
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
        eb.setTitle("C'est à la voyante de jouer !");
        eb.setFooter("Faites le bon choix !");
        eb.addField("Pour utilisez votre pouvoir:", "Cliquez sur le boutton ci-dessous", false);
        return eb;
    }

    private void sendMessage(Player player) {
        player.getGame().getChannelsManager().getVillageChannel().
                sendMessageEmbeds(getMessage().build())
                .addActionRow(
                        Button.success("seer", "Voir un role")
                ).complete();
    }

    private EmbedBuilder getRoleMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous venez d'utiliser votre pouvoir !");
        eb.addField("Le role de " + player.getMember().getEffectiveName() + " est :", player.getRole().getName(), false);
        eb.setFooter("Cela met donc fin à vos soupçons sur cette personne !");
        return eb;
    }

}
