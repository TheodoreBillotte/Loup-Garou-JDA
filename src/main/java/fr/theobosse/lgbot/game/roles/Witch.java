package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.util.ArrayList;
import java.util.HashMap;

public class Witch extends GameActions {

    private final HashMap<Player, Boolean> revive = new HashMap<>();
    private final HashMap<Player, Boolean> death = new HashMap<>();

    private final ArrayList<Player> playing = new ArrayList<>();

    @Override
    public void onPlay(Player player) {
        playing.add(player);
        this.revive.putIfAbsent(player, true);
        this.death.putIfAbsent(player, true);
        sendPlayMessage(player);
    }


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member.getUser());
        if (player == null) return;
        Game game = player.getGame();
        if (player.getRole() == null) return;
        if (player.getRole().getActions() == null) return;
        if (!player.getRole().getActions().equals(this)) return;
        if (!playing.contains(player)) return;

        if (event.getComponentId().equals("witch play")) {
            sendMessage(player, event);
        } else if (event.getComponentId().equals("revive")) {
            event.reply("Choisissez un joueur à ressuciter").addActionRow(
                    game.getMessages().getPlayerListSelectInteraction("revive", "Cible").build()
            ).setEphemeral(true).queue();
        } else if (event.getComponentId().equals("death")) {
            event.reply("Choisissez un joueur à tuer").addActionRow(
                    game.getMessages().getPlayerListSelectInteraction("death", "Cible").build()
            ).setEphemeral(true).queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member.getUser());
        if (player == null) return;
        if (!playing.contains(player)) return;
        Game game = player.getGame();
        if (!player.getRole().getActions().equals(this)) return;
        if (!playing.contains(player)) return;

        Player target = GamesInfo.getPlayer(game, event.getValues().get(0));
        if (target == null) return;
        if (!target.isAlive()) return;

        if (event.getComponentId().equals("revive") && game.getUtils().getKills().contains(target)) {
            if (revive.get(player)) {
                playing.remove(player);
                revive.put(player, false);
                game.getUtils().getKills().remove(target);
                sendActionMessage(target, "ressuscité", event);
            } else {
                event.reply("Vous avez déjà utilisé votre pouvoir").setEphemeral(true).queue();
            }
        } else if (event.getComponentId().equals("death")) {
            if (death.get(player)) {
                playing.remove(player);
                death.put(player, false);
                game.getUtils().getKills().add(target);
                sendActionMessage(target, "tué", event);
            } else {
                event.reply("Vous avez déjà utilisé votre pouvoir").setEphemeral(true).queue();
            }
        }
    }



    public EmbedBuilder getMessage(boolean revive, boolean death, Game game) {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("C'est à vous de jouer !");
        eb.setFooter("Faites le bon choix !");

        if (!game.getUtils().getKills().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Player k : game.getUtils().getKills())
                sb.append(k.getMember().getAsMention()).append("   ");
            eb.addField(game.getUtils().getKills().size() == 1 ? "La personne morte cette nuit est : " :
                    "Les personnes mortes cette nuit sont : ", sb.toString(), true);
        } else
            eb.addField("Personne n'a été tué jusqu'à présent !",
                    "Vous ne pouvez donc pas utiliser votre pouvoir de resurrection cette nuit !", true);

        eb.addBlankField(false);
        if (revive && !game.getUtils().getKills().isEmpty())
            eb.addField("Vous pouvez ressusciter quelqu'un avec", "Le boutton Ressusciter ci-dessous", false);

        if (death)
            eb.addField("Vous pouvez tuer quelqu'un avec", "Le boutton Tuer ci-dessous", false);

        if (!death && !revive)
            eb.addField("Vous avez déjà tout utilisé !", "Vous ne servez plus à rien maintenant !", false);
        eb.addBlankField(false);

        return eb;
    }

    public EmbedBuilder getPlayMessage() {
        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("C'est au tour de la sorcière !");
        eb.addField("Cliquez sur le boutton ci-dessous pour jouer !",
                "vous aurez aussi des informations sur la partie !", false);
        eb.setFooter("La partie est entre vos mains !");
        return eb;
    }

    public void sendMessage(Player player, ComponentInteraction event) {
        boolean revive = this.revive.get(player);
        boolean death = this.death.get(player);
        Button reviveButton = null;
        Button deathButton = null;

        if (revive && !player.getGame().getUtils().getKills().isEmpty())
            reviveButton = Button.primary("revive", "Ressusciter").withEmoji(Emotes.getEmote("bluepotion"));
        if (death)
            deathButton = Button.primary("death", "Tuer").withEmoji(Emotes.getEmote("orangepotion"));
        ReplyCallbackAction create = event.replyEmbeds(getMessage(revive, death, player.getGame()).build());
        if (reviveButton != null && deathButton != null)
            create.setActionRow(reviveButton, deathButton);
        else if (reviveButton != null)
            create.setActionRow(reviveButton);
        else if (deathButton != null)
            create.setActionRow(deathButton);
        create.setEphemeral(true).queue();
    }

    public void sendActionMessage(Player target, String action, ComponentInteraction event) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous venez de jouer !");
        eb.setFooter("Espérons que vous avez fait le bon choix !");

        eb.addField("Vous venez de " + action + " :", target.getMember().getEffectiveName(), false);
        event.replyEmbeds(eb.build()).setEphemeral(true).queue();
    }

    public void sendPlayMessage(Player player) {
        player.getGame().getChannelsManager().getVillageChannel().sendMessageEmbeds(getPlayMessage().build())
                .addActionRow(
                        Button.primary("witch play", "Jouer").withEmoji(Emotes.getEmote("play"))
                ).queue();
    }
}
