package fr.theobosse.lgbot.commands;

import fr.theobosse.lgbot.game.Game;
import fr.theobosse.lgbot.game.GamesInfo;
import fr.theobosse.lgbot.utils.Commands;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public class CreateCommand implements Commands {

    @Override
    public void onCommand(String message, Member sender, List<OptionMapping> args, MessageChannel channel) {
        if (args.size() >= 1) {
            if (GamesInfo.getPlayer(sender) == null)
                new Game(sender, args.get(0).getAsString());
        } else {
            channel.sendMessage("La commande entrée est incorrecte !").queue();
        }
    }
}
