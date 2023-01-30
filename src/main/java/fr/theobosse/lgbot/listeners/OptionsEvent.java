package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

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
                                Modal.create("dayTime", "Durée de la journée")
                                        .addActionRow(
                                                TextInput.create("dayTime", "Durée de la journée", TextInputStyle.SHORT)
                                                        .setPlaceholder("Durée de la journée")
                                                        .setMinLength(1)
                                                        .setMaxLength(3)
                                                        .build()
                                        )
                                        .build()).queue();
                        break;
                    case "night duration":
                        event.replyModal(
                                Modal.create("nightTime", "Durée de la nuit")
                                        .addActionRow(
                                                TextInput.create("nightTime", "Durée de la nuit", TextInputStyle.SHORT)
                                                        .setPlaceholder("Durée de la nuit")
                                                        .setMinLength(1)
                                                        .setMaxLength(3)
                                                        .build()
                                        )
                                        .build()).queue();
                        break;
                    case "kick":
                        event.reply("Choisissez la personne à kick !")
                                .addActionRow(
                                        game.getMessages().getPlayerListSelectInteraction("kick", "Kick")
                                                .build()
                                ).addActionRow(
                                        Button.danger("cancel", "Annuler")
                                )
                                .queue();
                        break;
                    case "ban":
                        event.reply("Choisissez la personne à bannir !")
                                .addActionRow(
                                        game.getMessages().getPlayerListSelectInteraction("ban", "Bannir")
                                                .build()
                                ).addActionRow(
                                        Button.danger("cancel", "Annuler")
                                )
                                .queue();
                        break;
                }
            }
        }
    }

}
