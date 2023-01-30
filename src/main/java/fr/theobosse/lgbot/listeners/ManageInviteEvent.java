package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Component;

import java.util.List;

public class ManageInviteEvent extends ListenerAdapter {

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (!event.getComponentType().equals(Component.Type.USER_SELECT)) return;
        if (!event.getComponentId().equals("addInvite")) return;

        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        List<Member> members = event.getMentions().getMembers();
        if (members.size() == 0) return;

        for (Member m : members)
            player.getGame().getOptions().addInvited(m);
        player.getGame().getMessages().updateInvitesMessages();
        player.getGame().getMessages().updateOptionsMessages();
        event.getMessage().delete().complete();
        event.deferEdit().queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (!event.getComponentId().equals("removeInvite")) return;

        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;
        List<String> members = event.getValues();
        if (members.size() == 0) return;

        for (String m : members)
            player.getGame().getOptions().removeInvited(member.getGuild().retrieveMemberById(m).complete());
        player.getGame().getMessages().updateInvitesMessages();
        player.getGame().getMessages().updateOptionsMessages();
        event.getMessage().delete().complete();
        event.deferEdit().queue();
    }

}
