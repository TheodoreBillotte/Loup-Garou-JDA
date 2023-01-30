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

public class ChangeNightTimeEvent extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (!event.getModalId().equals("nightTime")) return;
        Member member = event.getMember();
        int nightTime;

        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;

        try {
            nightTime = Integer.parseInt(Objects.requireNonNull(event.getValue("nightTime")).getAsString());
        }
        catch (Exception ignored) {
            return;
        }

        if (nightTime < 1) {
            event.reply("La durée de la nuit doit être supérieure à 0 !").setEphemeral(true).queue();
            return;
        }

        player.getGame().getOptions().setNightTime(nightTime);
        player.getGame().getMessages().updateOptionsMessages();
        event.deferEdit().queue();
    }
}
