package fr.theobosse.lgbot.listeners;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CancelAction extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (event.getComponentId().equals("cancel"))
            try {
                event.getMessage().delete().queue();
            } catch (Exception ignored) {}
    }
}
