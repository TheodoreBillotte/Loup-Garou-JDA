package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.utils.ChatManager;
import fr.theobosse.lgbot.utils.Messages;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class VoteEvent extends ListenerAdapter {

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannelType().equals(ChannelType.TEXT))
            return;
        TextChannel channel = event.getChannel().asTextChannel();
        Member member = event.getMember();
        Player player = GamesInfo.getPlayer(member);
        assert member != null;
        String action = ChatManager.getAction(member);

        if (player == null) return;
        if (action == null || !action.equalsIgnoreCase("vote")) return;
        if (!channel.equals(player.getGame().getChannelsManager().getVillageChannel())) return;

        Member m;
        try {
            m = event.getMessage().getMentions().getMembers().get(0);
        } catch (Exception ignored) { m = GamesInfo.getMember(event.getGuild(), event.getMessage().getContentDisplay()); }
        if (m == null) { Messages.sendErrorMessage(channel, "La personne entrée est incorrecte !", 3D); return; }

        ChatManager.removeAction(member);
        Player p = GamesInfo.getPlayer(m);
        Game game = player.getGame();
        player.getGame().getUtils().getVotes().putIfAbsent(p, 0);
        player.getGame().getUtils().getVotes().putIfAbsent(player, 0);
        Player oldP = game.getUtils().getVoters().get(player);

        if (oldP != null)
            player.getGame().getUtils().getVotes().put(oldP, player.getGame().getUtils().getVotes().get(oldP) - 1);

        player.getGame().getUtils().getVoters().put(player, p);
        player.getGame().getUtils().getVotes().put(p, player.getGame().getUtils().getVotes().get(p) + 1);
        player.getGame().getMessages().updateVotesMessages();
        event.getMessage().delete().complete();
    }

}
