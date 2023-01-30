package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoteButtonEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (!event.getComponentId().equals("vote")) return;
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        if (member == null) return;
        if (player == null) return;
        Game game = player.getGame();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        Member member = event.getMember();
        MessageChannel channel = event.getChannel();
        Emoji e = event.getReaction().getEmoji();
        Player player = GamesInfo.getPlayer(member);

        if (member == null) return;
        if (player == null) return;
        Game game = player.getGame();
        Message message = game.getMessagesManager().getVotesMessage();

        if (message == null) return;
        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (message.getEmbeds().size() == 1) {
            message.removeReaction(e, member.getUser()).complete();
            if (e.equals(Emoji.fromUnicode("💌"))) {
                String action = ChatManager.getAction(member);
                if (action == null || !action.equalsIgnoreCase("vote")) {
                    ChatManager.setAction(member, "vote");
                } else {
                    ChatManager.removeAction(member);
                }
            }
        }
    }
}
