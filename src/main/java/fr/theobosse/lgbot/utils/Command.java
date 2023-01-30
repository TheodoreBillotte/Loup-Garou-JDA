package fr.theobosse.lgbot.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public interface Command {

    default void onCommand(Message message, Member sender, String label, String[] args, TextChannel channel) {}
}
