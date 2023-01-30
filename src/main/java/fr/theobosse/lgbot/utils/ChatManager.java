package fr.theobosse.lgbot.utils;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatManager {
    private static final HashMap<User, String> players = new HashMap<>();

    public static String getAction(Member member) {
        return players.get(member.getUser());
    }

    public static String getAction(User user) {
        return players.get(user);
    }

    public static void setAction(Member member, String action) {
        players.put(member.getUser(), action);
    }

    public static void setAction(User user, String action) {
        players.put(user, action);
    }

    public static void removeAction(Member member) {
        players.remove(member.getUser());
    }

    public static void removeAction(User user) {
        players.remove(user);
    }

    public static HashMap<User, String> getPlayers() {
        return players;
    }

    public static List<User> getMembers() {
        return new ArrayList<>(players.keySet());
    }
}
