package fr.theobosse.lgbot.utils;

import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.game.Role;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;

import java.util.ArrayList;
import java.util.List;

public class Roles {

    private static final List<Role> roles = new ArrayList<>();

    public static List<Role> getRoles() {
        return roles;
    }

    public static void addRole(Role role) {
        roles.add(role);
    }

    public static void removeRole(Role role) {
        roles.remove(role);
    }

    public static Role getRoleByName(String name) {
        for (Role role : roles)
            if (role.getName().equalsIgnoreCase(name))
                return role;
        return null;
    }

    public static Role getRoleBySub(String subName) {
        for (Role role : roles)
            if (role.getSubName().equalsIgnoreCase(subName))
                return role;
        return null;
    }

    public static Role getRoleByRound(Rounds round) {
        for (Role role : roles)
            if (role.getRound().equals(round))
                return role;
        return null;
    }

    public static Role getRoleByEmote(CustomEmoji emote) {
        for (Role role : roles)
            if (role.getEmoji().getName().equals(emote.getName()))
                return role;
        return null;
    }

    public static int getRolesCount(Role role, List<Role> roles) {
        return (int) roles.stream().filter(role::equals).count();
    }
}
