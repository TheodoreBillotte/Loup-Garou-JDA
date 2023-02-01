package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.Objects;

public class OptionsEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        String e = event.getButton().getId();

        if (member == null || e == null) return;
        Player player = GamesInfo.getPlayer(member);

        if (player == null) return;
        Game game = player.getGame();
        Message message = game.getMessagesManager().getOptionsMessage();

        if (message == null) return;
        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (message.getEmbeds().size() == 1) {
            MessageEmbed embed = message.getEmbeds().get(0);
            if (Objects.requireNonNull(embed.getTitle()).equalsIgnoreCase("Les options")) {
                switch (e) {
                    case "invite":
                        try {
                            game.getMessages().deleteInvitesMessage();
                            event.deferEdit().queue();
                        } catch (Exception ignored) {
                            game.getMessages().sendInvitationsMessage(event);
                        }
                        break;
                    case "day duration":
                        event.replyModal(
                                Modal.create("dayTime", "Temps des votes")
                                        .addActionRow(
                                                TextInput.create("dayTime", "Temps de votes (en secondes)",
                                                                TextInputStyle.SHORT)
                                                        .setPlaceholder("Temps des votes")
                                                        .setMinLength(1)
                                                        .setMaxLength(3)
                                                        .build()
                                        )
                                        .build()).queue();
                        break;
                    case "night duration":
                        event.replyModal(
                                Modal.create("nightTime", "Temps de jeu")
                                        .addActionRow(
                                                TextInput.create("nightTime", "Temps de jeu de chaque rôle (en secondes)",
                                                                TextInputStyle.SHORT)
                                                        .setPlaceholder("Temps de jeu")
                                                        .setMinLength(1)
                                                        .setMaxLength(3)
                                                        .build()
                                        ).build()).queue();
                        break;
                    case "kick":
                        event.reply("Choisissez la personne à kick !")
                                .addActionRow(
                                        Messages.getPlayerListSelectInteraction(game.getUtils().getPlayers(),"kick", "Kick").build()
                                ).addActionRow(
                                        Button.danger("cancel", "Annuler")
                                ).queue();
                        break;
                    case "ban":
                        event.reply("Choisissez la personne à bannir !")
                                .addActionRow(
                                        Messages.getPlayerListSelectInteraction(game.getUtils().getPlayers(),"ban", "Bannir").build()
                                ).addActionRow(
                                        Button.danger("cancel", "Annuler")
                                ).queue();
                        break;
                    case "mute dead":
                        game.getOptions().setDeadAreMuted(!game.getOptions().isDeadAreMuted());
                        player.getGame().getMessages().updateOptionsMessages();
                        event.deferEdit().queue();
                        break;
                    case "mute night":
                        game.getOptions().setNightMute(!game.getOptions().isNightMute());
                        player.getGame().getMessages().updateOptionsMessages();
                        event.deferEdit().queue();
                        break;
                    case "max players":
                        event.replyModal(
                                Modal.create("maxPlayers", "Nombre de joueurs maximum")
                                        .addActionRow(
                                                TextInput.create("maxPlayers", "Nombre de joueurs maximum",
                                                                TextInputStyle.SHORT)
                                                        .setPlaceholder("1 - 25")
                                                        .setMinLength(1)
                                                        .setMaxLength(2)
                                                        .build()
                                        ).build()).queue();
                        break;
                }
            }
        }
    }

}
