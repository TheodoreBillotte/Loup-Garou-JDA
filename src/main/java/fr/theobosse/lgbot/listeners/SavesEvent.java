package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SavesEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().endsWith("save")) return;
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        if (event.getComponentId().equals("save")) {
            LGBot.getLoader().saveData(game);
            event.editMessageEmbeds(game.getMessages().getSavesMessage().build()).queue();
        } else if (event.getComponentId().equals("load save")) {
            event.reply("Choisissez la sauvegarde à charger")
                    .addActionRow(
                            LGBot.getLoader().getSaves(player, "load save")
                    ).addActionRow(
                            Button.danger("cancel", "Annuler")
                    ).queue();
        } else if (event.getComponentId().equals("delete save")) {
            event.reply("Choisissez la sauvegarde à supprimer")
                    .addActionRow(
                            LGBot.getLoader().getSaves(player, "delete save")
                    ).addActionRow(
                            Button.danger("cancel", "Annuler")
                    ).queue();
        }
    }



    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().endsWith("save")) return;
        String value = event.getValues().get(0);
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        Game game = player.getGame();

        if (event.getComponentId().equals("delete save")) {
            LGBot.getLoader().removeSave(game, value);
            event.editMessageEmbeds(game.getMessages().getSavesMessage().build()).queue();
        } else if (event.getComponentId().equals("load save")) {
            LGBot.getLoader().loadData(game, value);
            event.reply("Partie chargée !").setEphemeral(true).queue();

            game.getMessages().updateOptionsMessages();
            game.getMessages().updateRolesMessages();
            game.getMessages().updateWaitingMessages();
            game.getMessages().updateMainMessage();
            game.getMessages().updateInfoMessages();
            game.getChannelsManager().getCreationChannel().getManager().setName(value).queue();
        }
        event.getMessage().delete().queue();
    }

}
