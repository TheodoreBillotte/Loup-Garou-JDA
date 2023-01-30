package fr.theobosse.lgbot.listeners;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.game.Player;
import fr.theobosse.lgbot.game.Role;
import fr.theobosse.lgbot.utils.Roles;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class AddRoleEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return;
        Player player = GamesInfo.getPlayer(member);
        if (player == null) return;

        Game game = player.getGame();
        Message message = game.getMessagesManager().getAddRoleMessage();

        if (message == null) return;
        if (member.getUser().isBot()) return;
        if (!message.getId().equals(event.getMessageId())) return;
        if (message.getEmbeds().size() == 1) {
            MessageEmbed embed = message.getEmbeds().get(0);
            if (Objects.requireNonNull(embed.getTitle()).equalsIgnoreCase("Vous pouvez ajouter des roles !")) {
                Role role = Roles.getRoleByName(event.getButton().getId());
                if (role != null) {
                    game.getUtils().addRole(role);
                    event.deferEdit().queue();
                }
            }
        }
    }

}
