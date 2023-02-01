package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GameActions;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.HashMap;
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
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("werewolf")) return;
        Member member = event.getMember();
        String msgId = event.getMessageId();
        MessageChannel channel = event.getChannel();
        if (member == null) return;
        if (member.getUser().isBot()) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        if (messages.get(game) == null) return;
        if (game.getChannelsManager().getWerewolfChannel() == null) return;
        if (!channel.getId().equals(game.getChannelsManager().getWerewolfChannel().getId())) return;
        if (!player.getRole().getRound().equals(Rounds.WEREWOLF)) return;
        if (!msgId.equals(messages.get(game).getId())) return;

        event.reply("Choisissez votre cible")
                .addActionRow(
                        Messages.getPlayerListSelectInteraction(game.getUtils().getAlive(), "lgVote", "Votre cible").build()
                ).setEphemeral(true).queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("lgVote")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();
        if (game.getUtils().getRound() == null || !game.getUtils().getRound().equals(Rounds.WEREWOLF)) return;
        Player target = GamesInfo.getPlayer(game, event.getValues().get(0));
        if (target == null) return;

        vote(player, target);
        updateMessage(player);
        event.reply("Vous avez voté pour " + target.getMember().getAsMention()).setEphemeral(true).queue();
    }



    public EmbedBuilder getMessage(Player player) {
        Game game = player.getGame();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("C'est l'heure de voter !");
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

        eb.addField("Pour voter:", "cliquez sur le bouton ci-dessous et sélectionnez la personne pour qui vous " +
                "souhaitez voter",false);
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

        Message msg = game.getChannelsManager().getWerewolfChannel().sendMessageEmbeds(getMessage(player).build())
                .addActionRow(
                        Button.primary("werewolf", "Voter")
                ).complete();
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
        votes.putIfAbsent(game, new HashMap<>());
        votes.get(game).put(player, voted);
    }

    public long getVotes(Player voted) {
        return votes.get(voted.getGame()).values().stream().filter(voted::equals).count();
    }
}
