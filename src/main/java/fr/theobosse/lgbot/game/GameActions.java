package fr.theobosse.lgbot.game;

import net.dv8tion.jda.api.hooks.ListenerAdapter;

public abstract class GameActions extends ListenerAdapter {

    public void onPlay(Player player) {}

    public void onEndRound(Player player) {}

    public void onNightDeath(Player player) {}

    public void onVoteDeath(Player player) {}

    public void onVote(Player player) {}

    public void onBecomeMajor(Player player) {}

    public void onWakeUp(Player player) {}

    public void onSleep(Player player) {}
}
