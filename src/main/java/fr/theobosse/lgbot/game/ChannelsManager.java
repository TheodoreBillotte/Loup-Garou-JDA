package fr.theobosse.lgbot.game;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

public class ChannelsManager {

    private TextChannel creationChannel;
    private TextChannel waitingChannel;
    private TextChannel villageChannel;
    private TextChannel werewolfChannel;
    private VoiceChannel voiceChannel;


    public void setCreationChannel(TextChannel creationChannel) {
        this.creationChannel = creationChannel;
    }

    public void setWaitingChannel(TextChannel waitingChannel) {
        this.waitingChannel = waitingChannel;
    }

    public void setVillageChannel(TextChannel villageChannel) {
        this.villageChannel = villageChannel;
    }

    public void setWerewolfChannel(TextChannel werewolfChannel) {
        this.werewolfChannel = werewolfChannel;
    }

    public void setVoiceChannel(VoiceChannel voiceChannel) {
        this.voiceChannel = voiceChannel;
    }



    public TextChannel getCreationChannel() {
        return creationChannel;
    }

    public TextChannel getWaitingChannel() {
        return waitingChannel;
    }

    public TextChannel getVillageChannel() {
        return villageChannel;
    }

    public TextChannel getWerewolfChannel() {
        return werewolfChannel;
    }

    public VoiceChannel getVoiceChannel() {
        return voiceChannel;
    }
}
