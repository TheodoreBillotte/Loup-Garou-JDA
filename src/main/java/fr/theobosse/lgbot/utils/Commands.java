package fr.theobosse.lgbot.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.List;

public interface Commands {
    default void onCommand(String message, Member sender,
                           List<OptionMapping> args,
                           MessageChannel channel) {}
}
