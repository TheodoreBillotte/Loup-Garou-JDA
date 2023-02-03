package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.Role;
import fr.theobosse.lgbot.game.enums.GameState;
import fr.theobosse.lgbot.utils.Roles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class RemoveRoleEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;

        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;

        Game game = player.getGame();
        Message message = game.getMessagesManager().getRemoveRoleMessage();

        if (message == null) return;
        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (!game.getState().equals(GameState.WAITING)) return;
        Role r = Roles.getRoleByName(event.getButton().getId());
        if (r != null) {
            game.getUtils().removeRole(r);
            game.getMessages().updateRolesMessages();
            event.deferEdit().queue();
        }
    }
}
