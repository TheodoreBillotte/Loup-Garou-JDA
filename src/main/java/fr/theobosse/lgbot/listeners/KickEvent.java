package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class KickEvent extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("kick")) return;
        if (event.getMember() == null) return;
        if (event.getGuild() == null) return;

        List<String> users = event.getValues();
        if (users.size() == 0) return;

        Member m = event.getGuild().getMemberById(users.get(0));
        if (m == null) return;

        Player player = GamesInfo.getPlayer(event.getMember());
        if (player == null) return;

        if (player.getMember().equals(m)) {
            event.reply("Vous ne pouvez pas vous exclure vous-même !").setEphemeral(true).queue();
            return;
        }

        player.getGame().quit(m);
        player.getGame().getMessages().updateOptionsMessages();
        event.getMessage().delete().queue();
    }

}
