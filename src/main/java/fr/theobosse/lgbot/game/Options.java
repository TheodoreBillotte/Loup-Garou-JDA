package fr.theobosse.lgbot.game;

import net.dv8tion.jda.api.entities.Member;

import java.util.ArrayList;
import java.util.List;

public class Options {

    private boolean onInvite = true;
    private boolean deadAreMuted = true;
    private boolean nightMute = false;

    private int dayTime = 30;
    private int nightTime = 30;
    private int maxPlayers = 18;

    private final List<Member> invitedList = new ArrayList<>();
    private final List<Member> bannedList = new ArrayList<>();

    public boolean gameIsOnInvite() {
        return onInvite;
    }


    public boolean isBanned(Member member) {
        return bannedList.contains(member);
    }

    public boolean isInvited(Member member) {
        return invitedList.contains(member);
    }

    public boolean isDeadAreMuted() {
        return deadAreMuted;
    }

    public List<Member> getBannedList() {
        return bannedList;
    }

    public List<Member> getInvitedList() {
        return invitedList;
    }

    public int getNightTime() {
        return nightTime;
    }

    public int getDayTime() {
        return dayTime;
    }

    public boolean isNightMute() {
        return nightMute;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setInvite(boolean invite) {
        this.onInvite = invite;
    }

    public void setDeadAreMuted(boolean deadAreMuted) {
        this.deadAreMuted = deadAreMuted;
    }

    public void addInvited(Member member) {
        invitedList.add(member);
    }

    public void removeInvited(Member member) {
        invitedList.remove(member);
    }

    public void addBanned(Member member) {
        bannedList.add(member);
    }

    public void removeBanned(Member member) {
        bannedList.remove(member);
    }

    public void setDayTime(int dayTime) {
        this.dayTime = dayTime;
    }

    public void setNightTime(int nightTime) {
        this.nightTime = nightTime;
    }

    public void setNightMute(boolean nightMute) {
        this.nightMute = nightMute;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }
}
