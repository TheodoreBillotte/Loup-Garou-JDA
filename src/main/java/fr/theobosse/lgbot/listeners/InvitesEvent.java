package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;

import java.util.Objects;

public class InvitesEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        String e = event.getButton().getId();

        if (member == null || e == null) return;
        Player player = GamesInfo.getPlayer(member);

        if (player == null) return;
        Game game = player.getGame();
        Message message = game.getMessagesManager().getInvitesMessage();

        if (message == null) return;
        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (message.getEmbeds().size() == 1) {
            MessageEmbed embed = message.getEmbeds().get(0);
            if (Objects.requireNonNull(embed.getTitle()).equalsIgnoreCase("Les invitations")) {
                switch (e) {
                    case "activate":
                        game.getOptions().setInvite(!game.getOptions().gameIsOnInvite());
                        game.getMessages().updateInvitesMessages();
                        game.getMessages().updateInfoMessages();
                        if (game.getOptions().gameIsOnInvite())
                            event.reply("Les invitations sont maintenant activées !").setEphemeral(true).queue();
                        else
                            event.reply("Les invitations sont maintenant désactivée !").setEphemeral(true).queue();
                        break;
                    case "clear":
                        game.getOptions().getInvitedList().clear();
                        game.getMessages().updateInvitesMessages();
                        game.getMessages().updateOptionsMessages();
                        event.reply("Les invitations ont été vidés").setEphemeral(true).queue();
                        break;
                    case "add":
                        event.reply("Choisissez un joueur à ajouter aux invitations")
                                .addActionRow(
                                        EntitySelectMenu.create("addInvite", EntitySelectMenu.SelectTarget.USER)
                                                .setPlaceholder("Choisissez un joueur")
                                                .setRequiredRange(1, 25)
                                                .build()
                                ).queue();
                        break;
                    case "remove":
                        if (game.getOptions().getInvitedList().isEmpty()) {
                            event.reply("Les invitations sont vide").setEphemeral(true).queue();
                            return;
                        }
                        event.reply("Choisissez des joueurs à retirer des invitations")
                                .addActionRow(
                                        Messages.getListSelectInteraction(game.getOptions().getInvitedList(), "removeInvite", "Choisissez des joueurs")
                                                .setRequiredRange(1, 25)
                                                .build()
                                ).queue();
                        break;
                }
            }
        }
    }

}
