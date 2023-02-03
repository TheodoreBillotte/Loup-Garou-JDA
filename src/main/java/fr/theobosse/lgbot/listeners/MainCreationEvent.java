package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.enums.GameState;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Objects;

public class MainCreationEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        String e = event.getComponentId();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);

        if (player == null) return;
        Game game = player.getGame();
        Message message = event.getMessage();

        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (message.getEmbeds().size() == 1) {
            MessageEmbed embed = message.getEmbeds().get(0);
            if (Objects.requireNonNull(embed.getTitle()).equalsIgnoreCase("La partie " + game.getName() +
                    " de " + game.getHost().getEffectiveName() + " !")) {
                switch (e) {
                    case "add roles":
                        try {
                            game.getMessages().deleteAddRoleMessage();
                            event.deferEdit().queue();
                        } catch (Exception ignored) {
                            game.getMessages().sendAddRoleMessage(event);
                        }
                        break;
                    case "remove roles":
                        try {
                            game.getMessages().deleteRemoveRoleMessage();
                            event.deferEdit().queue();
                        } catch (Exception ignored) {
                            game.getMessages().sendRemoveRoleMessage(event);
                        }
                        break;
                    case "parameters":
                        try {
                            game.getMessages().deleteOptionsMessage();
                            event.deferEdit().queue();
                        } catch (Exception ignored) {
                            game.getMessages().sendOptionsMessage(event);
                        }
                        break;
                    case "host":
                        event.reply("Choisissez le nouvel host !")
                                .addActionRow(
                                        Messages.getPlayerListSelectInteraction(game.getUtils().getPlayers(), "host"
                                                        , "Choisissez le nouvel host !")
                                                .build()
                                ).addActionRow(
                                        Button.danger("cancel", "Annuler")
                                ).queue();
                        break;
                    case "remove":
                        GamesInfo.removePlayer(player);
                        game.getUtils().getPlayers().forEach(GamesInfo::removePlayer);
                        game.getChannelsManager().getCreationChannel().delete().complete();
                        game.getChannelsManager().getWaitingChannel().delete().complete();
                        game.getMessagesManager().getInfoMessage().delete().complete();
                        GamesInfo.removeGame(game);
                        break;
                    case "start":
                        if (game.getState().equals(GameState.WAITING)) {
                            if (game.getGameRunning().starting())
                                event.reply("La partie va commencer !").setEphemeral(true).queue();
                            else
                                event.reply("Il n'y a pas assez de joueurs pour démarrer la partie !")
                                        .setEphemeral(true).queue();
                        } else {
                            game.setState(GameState.WAITING);
                            event.reply("Le démarrage a été annulé !").setEphemeral(true).queue();
                        }
                        break;
                    case "saves":
                        try {
                            game.getMessages().deleteSavesMessage();
                            event.deferEdit().queue();
                        } catch (Exception ignored) {
                            game.getMessages().sendSavesMessage(event);
                        }
                        break;
                }
            }
        }
    }

}
