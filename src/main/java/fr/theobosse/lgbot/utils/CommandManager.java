package fr.theobosse.lgbot.utils;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class CommandManager extends ListenerAdapter {
    private static final HashMap<String, Commands> commands = new HashMap<>();

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getChannelType().isGuild()) return;
        if (!commands.containsKey(event.getName())) return;
        event.reply("Commande en cours d'exécution...").setEphemeral(true).queue();
        try {
            commands.get(event.getName()).onCommand(
                    event.getCommandString(),
                    event.getMember(),
                    event.getOptions(),
                    event.getMessageChannel()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
    }

    public void addCommand(String cmd, Commands cmdExecutor) {
        commands.put(cmd, cmdExecutor);
    }

    public void removeCommand(String cmd) {
        commands.remove(cmd);
    }
}
