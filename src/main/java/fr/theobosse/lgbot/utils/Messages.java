package fr.theobosse.lgbot.utils;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.Role;
import fr.theobosse.lgbot.game.enums.GameState;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Messages {
    private final Game game;

    public Messages(Game game) {
        this.game = game;
    }

    public EmbedBuilder getGameMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("La partie " + game.getName() + " de " + (game.getHost().getNickname() == null ? game.getHost().getEffectiveName() : game.getHost().getNickname()) + " !");
        eb.setAuthor(game.getHost().getNickname());
        eb.setThumbnail(game.getHost().getUser().getEffectiveAvatarUrl());
        eb.setColor(game.getState().equals(GameState.STARTING) ? Color.GREEN : Color.ORANGE);
        eb.setFooter("Vous pouvez cliquer sur les boutons ci-dessous pour " +
                "sélectionner des actions.", "https://images.emojiterra.com/openmoji/v12.2/128px/1f43a.png");

        eb.addBlankField(false);
        if (game.getState().equals(GameState.STARTING)) {
            eb.addField("VOTRE PARTIE DÉMARRERA DANS", String.valueOf(game.getStartTime()), false);
            eb.addBlankField(false);
        }

        eb.addField("Joueurs de la partie:", "Il y a " + game.getUtils().getPlayers().size() + " joueur(s).", false);
        for (Player player : game.getUtils().getPlayers()) {
            eb.addField(player.getMember().getEffectiveName(), player.getGame().getHost().equals(player.getMember()) ? "HOST" : "JOUEUR", true);
        }

        eb.addBlankField(false);

        return eb;
    }

    public EmbedBuilder getAddRolesMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous pouvez ajouter des roles !");
        eb.setAuthor(game.getHost().getNickname());
        eb.setColor(game.getHost().getColor());
        eb.setFooter("Vous pouvez cliquer sur les boutons ci-dessous pour " +
                "sélectionner des roles.");
        eb.addBlankField(false);
        List<Role> usedRoles = new ArrayList<>();
        game.getUtils().getRoles().forEach(role -> {
            if (!usedRoles.contains(role)) {
                usedRoles.add(role);
                eb.addField(role.getSubName() + " (" + Emotes.getString(role.getEmoji()) + ")", String.valueOf(Roles.getRolesCount(role, game.getUtils().getRoles())), true);
            }
        });

        if (game.getUtils().getRoles().isEmpty())
            eb.addField("Il n'y a aucun roles dans la partie !", "Mais vous pouvez en ajouter en cliquant ci-dessous !", false);

        eb.addBlankField(false);
        return eb;
    }

    public EmbedBuilder getRemoveRolesMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Vous pouvez retirer des roles !");
        eb.setAuthor(game.getHost().getNickname());
        eb.setColor(game.getHost().getColor());
        eb.setFooter("Vous pouvez cliquer sur les boutons ci-dessous pour sélectionner des roles.");
        eb.addBlankField(false);
        List<Role> usedRoles = new ArrayList<>();
        game.getUtils().getRoles().forEach(role -> {
            if (!usedRoles.contains(role)) {
                usedRoles.add(role);
                eb.addField(role.getSubName() + " (" + Emotes.getString(role.getEmoji()) + ")", String.valueOf(Roles.getRolesCount(role, game.getUtils().getRoles())), true);
            }
        });

        if (game.getUtils().getRoles().isEmpty())
            eb.addField("Il n'y a aucun roles dans la partie !", "Mais vous pouvez en ajouter en cliquant sur le bouton ✅ ci-dessus!", false);

        eb.addBlankField(false);
        return eb;
    }

    public EmbedBuilder getOptionsMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        StringBuilder sb = new StringBuilder();

        eb.setTitle("Les options");
        eb.setColor(game.getHost().getColor());
        eb.setThumbnail(Objects.requireNonNull(Emotes.getEmote("crow")).getImageUrl());
        eb.setFooter("Vous pouvez cliquer sur les boutons ci-dessous pour modifier des options.");

        eb.addField("Durée des votes (s)", String.valueOf(game.getOptions().getDayTime()), true);
        eb.addBlankField(true);
        eb.addField("Temps de chaque rôle (s)", String.valueOf(game.getOptions().getNightTime()), true);
        eb.addField("Mute les joueurs morts", game.getOptions().isDeadAreMuted() ? "Oui" : "Non", true);
        eb.addBlankField(true);
        eb.addField("Mute lors de la nuit", game.getOptions().isNightMute() ? "Oui" : "Non", true);
        eb.addField("Joueurs max", String.valueOf(game.getOptions().getMaxPlayers()), true);
        eb.addBlankField(false);

        game.getOptions().getInvitedList().forEach(m -> sb.append(m.getEffectiveName()));
        eb.addField("Invités:", sb.length() > 0 ? sb.toString() : "Aucun", true);
        eb.addBlankField(false);

        return eb;
    }

    public EmbedBuilder getInvitationsMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Les invitations");
        eb.setColor(game.getHost().getColor());
        eb.setThumbnail(Objects.requireNonNull(Emotes.getEmote("fox")).getImageUrl());
        eb.setFooter("Vous pouvez cliquer sur les boutons ci-dessous pour gérer les invitations.");

        List<Member> invited = game.getOptions().getInvitedList();
        eb.addField("Invités:", "Il y a " + invited.size() + " invité(s) !", false);
        invited.forEach(member -> eb.addField(member.getEffectiveName(), "EN ATTENTE...", true));
        if (invited.isEmpty()) eb.addField("Personne n'a été invité !", "Mais vous pouvez en ajouter en cliquant sur les boutons ci-dessous", true);

        eb.addField("La partie est", game.getOptions().gameIsOnInvite() ? "FERME" : "OUVERTE", false);

        eb.addBlankField(false);

        return eb;
    }

    public EmbedBuilder getWaitingMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(game.getState().equals(GameState.STARTING) ? Color.GREEN : Color.ORANGE);
        eb.setTitle("Salle d'attente");
        eb.setFooter("Patientez s'il vous plaît...");
        eb.addField("Pour quitter:", "cliquez sur le bouton ci-dessous", true);

        if (game.getState().equals(GameState.STARTING))
            eb.addField("DÉMARRE DANS", String.valueOf(game.getStartTime()), false);
        return eb;
    }

    public EmbedBuilder getInfoMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(game.getState().equals(GameState.WAITING) &&
                (game.getUtils().getPlayers().size() < game.getOptions().getMaxPlayers()) ? Color.GREEN : Color.RED);
        eb.setTitle("La partie " + game.getName() + " de " + game.getHost().getEffectiveName());
        eb.setFooter("Vous pouvez rejoindre la partie en cliquant sur le boutons ci-dessous !");
        eb.addField("Il y a actuellement " + game.getUtils().getPlayers().size() + " / " +
                        game.getOptions().getMaxPlayers() + " " + "joueur(s).",
                "Vous pouvez rejoindre avec le boutons ci-dessous !", false);

        if (game.getOptions().gameIsOnInvite())
            eb.addField("Il faut être invité pour pouvoir y entrer...", "Demandez à l'host si vous ne l'êtes pas !", false);

        if (game.getState().equals(GameState.WAITING))
            eb.addField("La partie est en attente de joueurs...", "N'hésitez pas à la rejoindre !", false);
        else
            eb.addField("La partie est en train d'être lancé !", "Il est désormais trop tard pour rejoindre !", false);

        return eb;
    }

    public EmbedBuilder getStartMessage() {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("Bienvenu dans la partie " + game.getName() + " de " + game.getHost().getEffectiveName());
        eb.setFooter("Bon jeu et bonne chance !");

        StringBuilder sb = new StringBuilder();
        for (Role role : game.getUtils().getRoles())
            sb.append(role.getName()).append(" - ");
        sb.append("Maire");

        eb.addField("Pour obtenir votre role:", "cliquez sur le bouton ci-dessous !", false);
        eb.addField("Les roles présents dans la partie sont:", sb.toString(), false);


        return eb;
    }

    public EmbedBuilder getVotesMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE);
        eb.setTitle("C'est le moment de voter !");
        eb.setFooter("Vous pouvez voter en cliquant sur le boutons ci-dessous !");
        eb.addField("Le nombre de votes:", "Si vous n'avez pas voté, n'hésitez pas !", false);

        for (Player player : game.getUtils().getAlive())
            eb.addField(player.getMember().getEffectiveName(), game.getUtils().getVotes().get(player) == null ? "0" : String.valueOf(game.getUtils().getVotes().get(player)), true);

        eb.addField("Pour voter", "sélectionnez la votre cible dans le menu ci-dessous", false);
        return eb;
    }

    public EmbedBuilder getSleepMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Le village s'endort !");
        eb.setFooter("Bonne nuit 😴 !");

        return eb;
    }

    public EmbedBuilder getWakeUpMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Le village se réveil !");
        eb.setFooter("Bonjour 😀 !");

        return eb;
    }

    public EmbedBuilder getKillsMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        if (game.getUtils().getKills().isEmpty()) {
            eb.setTitle("Personne n'a été tué cette nuit !");
            eb.setFooter("Génial ! 😃");
        } else {
            eb.setTitle("Le village est sous le choc !");
            if (game.getUtils().getKills().size() > 1)
                eb.setFooter("Paix à leurs âmes ! 😓");
            else
                eb.setFooter("Paix à son âme ! 😓");

            for (Player p : game.getUtils().getKills()) {
                String name = p.getMember().getEffectiveName();
                eb.addField(name, p.getRole().getName(), true);
            }
        }

        return eb;
    }

    public EmbedBuilder getEndMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle("La partie est terminée !");
        eb.addField("J'espère que vous avez aimé l'experience de jeu !",
                game.getUtils().hasWinner() ? "Le gagnant est " + game.getUtils().getWinner().name() :
                "La partie se termine sur une égalité !",
                false);
        eb.setFooter("Les channels seront supprimés dans 30s !");
        return eb;
    }

    public EmbedBuilder getDeathMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(player.getMember().getEffectiveName() + " a été tué lors des votes !");
        eb.setFooter("étais-ce un bon choix ?");

        eb.addField("Le role de " + player.getMember().getEffectiveName() + " était :", player.getRole().getName() + " !", false);
        return eb;
    }

    public EmbedBuilder getPlayerRoleMessage(Player player) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Bienvenue dans la partie " + game.getName() + " !");
        eb.setFooter("Amusez-vous bien !");

        eb.addField("Votre role pour cette partie est:", player.getRole().getName(), false);
        eb.addField("Votre objectif: ", player.getRole().getDescription(), false);
        return eb;
    }

    public EmbedBuilder getMajorMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.ORANGE);
        eb.setTitle("Il est temps de choisir le nouveau maire !");
        for (Player player : game.getUtils().getAlive())
            eb.addField(player.getMember().getEffectiveName(), game.getUtils().getVotes().get(player) == null ? "0" :
                    String.valueOf(game.getUtils().getMajorVotes().get(player)), true);

        eb.addField("Pour choisir le nouveau maire:",
                "Sélectionnez la personne pour laquelle vous souhaitez voter dans le menu déroulant ci-dessous !",
                false);
        return eb;
    }

    private EmbedBuilder getElectMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Le maire a été élu !");
        builder.setDescription("Le maire est " + game.getUtils().getMajor().getMember().getAsMention() + " !");
        builder.setColor(Color.GREEN);
        return builder;
    }

    private EmbedBuilder getMajorDeathMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setTitle("Le maire est mort");
        builder.setDescription("C'est à l'ancien maire de choisir qui sera son successeur !");
        builder.setFooter("C'est une perte tragique !");
        return builder;
    }

    public EmbedBuilder getSavesMessage() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Sauvegardes");
        builder.setColor(Color.GREEN);
        builder.setDescription("Sauvegardes disponibles :");
        builder.setFooter("Sauvegardes");

        LGBot.getLoader().getJson().get(game.getHost().getId()).fieldNames().forEachRemaining(s ->
                builder.addField(s, "cliquez sur les boutons pour charger / supprimer cette sauvegarde", false)
        );

        return builder;
    }

    private EmbedBuilder getLoveDeathMessage(Player player, Player lover) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle("Mort d'amour");
        builder.setDescription(player.getMember().getAsMention() + " est mort d'amour pour " + lover.getMember().getAsMention());
        builder.setColor(Color.RED);
        return builder;
    }



    public void sendMainMessage() {
        Message msg = game.getChannelsManager().getCreationChannel().sendMessageEmbeds(getGameMessage().build()).
                        addActionRow(
                                Button.primary("add roles", "Ajouter des rôles"),
                                Button.primary("remove roles", "Retirer des rôles"),
                                Button.primary("parameters", "Paramètres"),
                                Button.primary("host", "Choisir l'hôte")
                        ).
                        addActionRow(
                                Button.success("start", "Lancer la partie"),
                                Button.danger("remove", "Supprimer la partie"),
                                Button.secondary("saves", "Sauvegardes")
                        ).
                        complete();
        game.getMessagesManager().setMainCreationMessage(msg);
    }

    public void sendAddRoleMessage(ComponentInteraction interaction) {
        ReplyCallbackAction reply = interaction.replyEmbeds(getAddRolesMessage().build());
        Button[] buttons = new Button[0];
        int i;

        for (i = 0; i < Roles.getRoles().size(); i++) {
            if (i % 5 == 0)
                buttons = new Button[Math.min(5, Roles.getRoles().size() - i)];
            Role role = Roles.getRoles().get(i);
            Button b = Button.
                    primary(role.getName(), role.getName()).
                    withEmoji(role.getEmoji());
            buttons[i % 5] = b;
            if (i % 5 == 4 || i == Roles.getRoles().size() - 1)
                reply.addActionRow(buttons);
        }

        InteractionHook msg = reply.complete();
        game.getMessagesManager().setAddRoleMessage(msg.retrieveOriginal().complete());
    }

    public void sendRemoveRoleMessage(ComponentInteraction interaction) {
        ReplyCallbackAction reply = interaction.replyEmbeds(getRemoveRolesMessage().build());
        Button[] buttons = new Button[0];
        int i;

        for (i = 0; i < Roles.getRoles().size(); i++) {
            if (i % 5 == 0)
                buttons = new Button[Math.min(5, Roles.getRoles().size() - i)];
            Role role = Roles.getRoles().get(i);
            Button b = Button.
                    primary(role.getName(), role.getName()).
                    withEmoji(role.getEmoji());
            buttons[i % 5] = b;
            if (i % 5 == 4 || i == Roles.getRoles().size() - 1)
                reply.addActionRow(buttons);
        }

        InteractionHook msg = reply.complete();
        game.getMessagesManager().setRemoveRoleMessage(msg.retrieveOriginal().complete());
    }

    public void sendOptionsMessage(ComponentInteraction interaction) {
        Message msg = interaction.replyEmbeds(getOptionsMessage().build())
                        .addActionRow(
                                Button.primary("invite", "Invitations"),
                                Button.primary("day duration", "Temps des votes"),
                                Button.primary("night duration", "Temps de jeu"),
                                Button.primary("max players", "Joueurs max")
                        )
                        .addActionRow(
                                Button.primary("mute dead", "Mute joueurs morts"),
                                Button.primary("mute night", "Mute durant la nuit"),
                                Button.danger("kick", "Kick"),
                                Button.danger("ban", "Ban")
                        )
                        .complete().retrieveOriginal().complete();
        game.getMessagesManager().setOptionsMessage(msg);
    }

    public void sendInvitationsMessage(ComponentInteraction interaction) {
        Message msg = interaction.replyEmbeds(getInvitationsMessage().build())
                .addActionRow(
                        Button.primary("add", "Ajouter"),
                        Button.primary("remove", "Retirer"),
                        Button.primary("activate", "Activer / Désactiver"),
                        Button.primary("clear", "Vider")
                ).complete().retrieveOriginal().complete();
        game.getMessagesManager().setInvitesMessage(msg);

    }

    public void sendWaitingMessage() {
        Message msg = game.getChannelsManager().getWaitingChannel().sendMessageEmbeds(getWaitingMessage().build())
                .addActionRow(
                        Button.danger("leave", "Quitter la partie")
                ).complete();
        game.getMessagesManager().setWaitingMessage(msg);
    }

    public void sendInfoMessage() {
        Message msg = GuildManager.getChannel().sendMessageEmbeds(getInfoMessage().build())
                .addActionRow(
                        Button.primary("join", "Rejoindre la partie")
                ).complete();
        game.getMessagesManager().setInfoMessage(msg);
    }

    public void sendStartMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getStartMessage().build())
                        .addActionRow(
                                Button.primary("role", "Voir son rôle")
                        ).queue();
    }

    public void sendVotesMessage() {
        Message msg = game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getVotesMessage().build())
                        .addActionRow(
                                getPlayerListSelectInteraction(game.getUtils().getAlive(), "vote", "Personne à voter").build()
                        ).complete();
        game.getMessagesManager().setVotesMessage(msg);
    }

    public void sendSleepMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getSleepMessage().build()).complete();
    }

    public void sendWakeUpMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getWakeUpMessage().build()).complete();
    }

    public void sendKillsMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getKillsMessage().build()).complete();
    }

    public void sendEndMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getEndMessage().build()).complete();
    }

    public void sendDeathMessage(Player player) {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getDeathMessage(player).build()).complete();
    }

    public void sendPlayerRoleMessage(Player player, ComponentInteraction interaction) {
        interaction.replyEmbeds(getPlayerRoleMessage(player).build()).setEphemeral(true).queue();
    }

    public void sendMajorMessage() {
        game.getMessagesManager().setMajorMessage(
                game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getMajorMessage().build())
                .addActionRow(
                        getPlayerListSelectInteraction(game.getUtils().getAlive(), "vote major",
                                "Personne à désigner").build()
                ).complete());
    }

    public void sendElectMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getElectMessage().build()).queue();
    }

    public void sendMajorDeathMessage() {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getMajorDeathMessage().build())
                .addActionRow(
                        getPlayerListSelectInteraction(game.getUtils().getAlive(), "major",
                                "Personne à désigner").build()
                ).complete();
    }

    public void sendSavesMessage(ButtonInteractionEvent event) {
        game.getMessagesManager().setSavesMessage(event.replyEmbeds(getSavesMessage().build())
                .addActionRow(
                        Button.primary("save", "Sauvegarder la partie"),
                        Button.success("load save", "Charger une sauvegarde"),
                        Button.danger("delete save", "Supprimer une sauvegarde")
                ).complete().retrieveOriginal().complete());
    }

    public void sendLoveDeathMessage(Player player, Player lover) {
        game.getChannelsManager().getVillageChannel().sendMessageEmbeds(getLoveDeathMessage(player, lover).build()).complete();
    }

    public void deleteAddRoleMessage() {
        game.getMessagesManager().getAddRoleMessage().delete().complete();
    }

    public void deleteRemoveRoleMessage() {
        game.getMessagesManager().getRemoveRoleMessage().delete().complete();
    }

    public void deleteOptionsMessage() {
        game.getMessagesManager().getOptionsMessage().delete().complete();
    }

    public void deleteInvitesMessage() {
        game.getMessagesManager().getInvitesMessage().delete().complete();
    }

    public void deleteInfoMessage() {
        game.getMessagesManager().getInfoMessage().delete().complete();
    }

    public void deleteSavesMessage() {
        game.getMessagesManager().getSavesMessage().delete().complete();
    }



    public void updateMainMessage() {
        try {
            game.getMessagesManager().getMainCreationMessage().editMessageEmbeds(getGameMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateRolesMessages() {
        try {
            game.getMessagesManager().getAddRoleMessage().editMessageEmbeds(getAddRolesMessage().build()).complete();
        } catch (Exception ignored) {}

        try {
            game.getMessagesManager().getRemoveRoleMessage().editMessageEmbeds(getRemoveRolesMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateOptionsMessages() {
        try {
            game.getMessagesManager().getOptionsMessage().editMessageEmbeds(getOptionsMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateInvitesMessages() {
        try {
            game.getMessagesManager().getInvitesMessage().editMessageEmbeds(getInvitationsMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateWaitingMessages() {
        try {
            game.getMessagesManager().getWaitingMessage().editMessageEmbeds(getWaitingMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateInfoMessages() {
        try {
            game.getMessagesManager().getInfoMessage().editMessageEmbeds(getInfoMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateVotesMessages() {
        try {
            game.getMessagesManager().getVotesMessage().editMessageEmbeds(getVotesMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateMajorMessages() {
        try {
            game.getMessagesManager().getMajorMessage().editMessageEmbeds(getMajorMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public void updateSavesMessage() {
        try {
            game.getMessagesManager().getSavesMessage().editMessageEmbeds(getSavesMessage().build()).complete();
        } catch (Exception ignored) {}
    }

    public static SelectMenu.Builder<StringSelectMenu, StringSelectMenu.Builder> getPlayerListSelectInteraction(List<Player> players, String id, String placeholder) {
        StringSelectMenu.Builder builder = StringSelectMenu.create(id).setPlaceholder(placeholder);
        for (Player player : players)
            builder.addOption(
                    player.getMember().getEffectiveName(),
                    player.getMember().getId(),
                    player.getMember().getUser().getAsTag()
            );
        return builder;
    }

    public static SelectMenu.Builder<StringSelectMenu, StringSelectMenu.Builder>
            getListSelectInteraction(List<Member> members, String id, String placeholder) {
        StringSelectMenu.Builder builder = StringSelectMenu.create(id).setPlaceholder(placeholder);
        for (Member member : members)
            builder.addOption(member.getEffectiveName(), member.getId(), member.getUser().getAsTag());
        return builder;
    }
}
