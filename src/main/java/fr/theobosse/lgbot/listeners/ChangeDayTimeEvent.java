package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChangeDayTimeEvent extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("dayTime")) return;
        Member member = event.getMember();
        int dayTime;

        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;

        try {
            dayTime = Integer.parseInt(Objects.requireNonNull(event.getValue("dayTime")).getAsString());
        }
        catch (Exception ignored) {
            return;
        }

        if (dayTime < 1) {
            event.reply("La durée de la journée doit être supérieure à 0 !").setEphemeral(true).queue();
            return;
        }

        player.getGame().getOptions().setDayTime(dayTime);
        player.getGame().getMessages().updateOptionsMessages();
        event.deferEdit().queue();
    }
}
