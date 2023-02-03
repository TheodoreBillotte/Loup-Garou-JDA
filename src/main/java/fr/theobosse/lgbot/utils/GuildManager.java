package fr.theobosse.lgbot.utils;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.Game;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GuildManager {

    private static final HashMap<Guild, Category> categories = new HashMap<>();
    private static TextChannel channel;

    public static boolean containsCategory(Guild guild, String name) {
        for (Category c : guild.getCategories())
            if (c.getName().equalsIgnoreCase(name)) {
                HashMap<String, Category> map = new HashMap<>();
                if (!LGBot.categories.containsKey(guild)) {
                    map.put(name, c);
                    LGBot.categories.put(guild, map);
                } else {
                    LGBot.categories.get(guild).put(name, c);
                }
                return true;
            }
        return false;
    }

    private static boolean containsInfoChannel(Guild guild) {
        if (channel != null) return true;
        for (GuildChannel c : guild.getChannels()) {
            if (c.getName().equalsIgnoreCase("\uD835\uDDDA\uD835\uDDEE\uD835\uDDFA\uD835\uDDF2\uD835\uDE00-\uD835\uDDDC\uD835\uDDFB\uD835\uDDF3\uD835\uDDFC\uD835\uDE00")) {
                guild.getTextChannels().forEach(tc  -> {
                    if (c.getId().equals(tc.getId())) channel = tc;
                });
                return true;
            }
        }

        return false;
    }



    public static void createCategory(Guild guild, String name) {
        if (containsCategory(guild, name)) return;
        HashMap<String, Category> map = new HashMap<>();
        Category c = guild.createCategory(name).complete();
        if (!LGBot.categories.containsKey(guild)) {
            map.put(name, c);
            LGBot.categories.put(guild, map);
        } else {
            LGBot.categories.get(guild).put(name, c);
        }
    }

    public static TextChannel createGameCreationChannel(Game game) {
        Guild guild = game.getGuild();
        return guild.createTextChannel(game.getName()).
                setParent(getGuildCategory(guild, "GameCreation")).
                setTopic("Ce channel sert à la création de la partie de " + game.getHost().getEffectiveName() + " !").
                addRolePermissionOverride(
                        game.getGuild().getPublicRole().getIdLong(),
                        new ArrayList<>(),
                        Arrays.asList(Permission.values())).
                addMemberPermissionOverride(
                        game.getHost().getIdLong(),
                        getHostAllowedPermissions(),
                        getHostDisallowedPermissions()
                ).
                complete();
    }

    public static void createInfoChannel(Guild guild) {
        if (containsInfoChannel(guild)) return;
        channel = guild.createTextChannel("\uD835\uDDDA\uD835\uDDEE\uD835\uDDFA\uD835\uDDF2\uD835\uDE00-\uD835\uDDDC\uD835\uDDFB\uD835\uDDF3\uD835\uDDFC\uD835\uDE00").complete();
    }

    public static TextChannel createWaitingChannel(Game game) {
        Guild guild = game.getGuild();
        Category wc = LGBot.categories.get(guild).get("Salles d'attente");
        return wc.createTextChannel("Host de " + game.getHost().getEffectiveName()).
                addMemberPermissionOverride(game.getHost().getIdLong(), getBasicAllowedPermissions(), getBasicDisallowedPermissions()).
                addRolePermissionOverride(675285855249629244L, new ArrayList<>(), Arrays.asList(Permission.values())).
                complete();
    }

    public static TextChannel createVillageChannel(Game game) {
        Guild guild = game.getGuild();
        Category wc = LGBot.categories.get(guild).get("Games");
        TextChannel channel = wc.createTextChannel("Village").
                addRolePermissionOverride(675285855249629244L, new ArrayList<>(), Arrays.asList(Permission.values())).
                complete();

        game.getUtils().getPlayers().forEach(p -> {
            Member m = p.getMember();
            channel.upsertPermissionOverride(m)
                    .setAllowed(getHostAllowedPermissions())
                    .setDenied(getHostDisallowedPermissions())
                    .complete();
        });

        return channel;
    }

    public static TextChannel createWereWolfChannel(Game game) {
        Guild guild = game.getGuild();
        Category wc = LGBot.categories.get(guild).get("Games");
        TextChannel channel = wc.createTextChannel("Loup-Garou").
                addRolePermissionOverride(675285855249629244L, new ArrayList<>(), Arrays.asList(Permission.values())).
                complete();

        game.getUtils().getPlayers().forEach(p -> {
            if (p.getRole().equals(Roles.getRoleByName("Loup-Garou")) || p.getRole().equals(Roles.getRoleByName("Loup-Garou Blanc"))) {
                Member m = p.getMember();
                channel.upsertPermissionOverride(m)
                        .setAllowed(getHostAllowedPermissions())
                        .setDenied(getHostDisallowedPermissions())
                        .complete();
            }
        });

        return channel;
    }

    public static VoiceChannel createVoiceChannel(Game game) {
        Guild guild = game.getGuild();
        Category wc = LGBot.categories.get(guild).get("Games");
        VoiceChannel v = wc.createVoiceChannel("Vocal de la partie").
                addRolePermissionOverride(game.getGuild().getPublicRole().getIdLong(),
                        new ArrayList<>(),
                        Arrays.asList(Permission.values())).
                complete();

        game.getUtils().getPlayers().forEach(p -> {
            Member m = p.getMember();
            v.upsertPermissionOverride(m)
                    .setAllowed(getVoiceAllowedPermissions())
                    .complete();
        });

        return v;
    }

    public static TextChannel getChannel() {
        return channel;
    }

    public static List<Permission> getBasicAllowedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.VIEW_CHANNEL);
        permissions.add(Permission.MESSAGE_ADD_REACTION);
        permissions.add(Permission.MESSAGE_HISTORY);
        return permissions;
    }

    public static List<Permission> getBasicDisallowedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.CREATE_INSTANT_INVITE);
        permissions.add(Permission.MESSAGE_EXT_EMOJI);
        permissions.add(Permission.MESSAGE_SEND);
        return permissions;
    }

    public static List<Permission> getHostAllowedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.VIEW_CHANNEL);
        permissions.add(Permission.MESSAGE_ADD_REACTION);
        permissions.add(Permission.MESSAGE_HISTORY);
        permissions.add(Permission.MESSAGE_SEND);
        return permissions;
    }

    public static List<Permission> getHostDisallowedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.CREATE_INSTANT_INVITE);
        permissions.add(Permission.MESSAGE_EXT_EMOJI);
        return permissions;
    }

    public static List<Permission> getVoiceAllowedPermissions() {
        List<Permission> permissions = new ArrayList<>();
        permissions.add(Permission.VOICE_SPEAK);
        permissions.add(Permission.VIEW_CHANNEL);
        permissions.add(Permission.VOICE_CONNECT);
        return permissions;
    }

    public static Category getGuildCategory(Guild guild, String name) {
        return LGBot.categories.get(guild).get(name);
    }

    public static HashMap<Guild, Category> getCategories() {
        return categories;
    }

    public static void addWaitingPermissions(Game game, Member member) {
        game.getChannelsManager().getWaitingChannel().upsertPermissionOverride(member).
                setAllowed(getBasicAllowedPermissions()).
                setDenied(getBasicDisallowedPermissions()).
                complete();
    }

    public static void removeWaitingPermissions(Game game, Member member) {
        game.getChannelsManager().getWaitingChannel().upsertPermissionOverride(member).clear().complete();
    }

    public static void modifyGameCreationChannel(Game game, Member oldHost) {
        game.getChannelsManager().getCreationChannel().getManager().
                setTopic("Ce channel sert à la création de la partie de " + game.getHost().getEffectiveName() + " !").
                removePermissionOverride(oldHost).
                putPermissionOverride(game.getHost(), getHostAllowedPermissions(), getHostDisallowedPermissions()).
                complete();
    }
}
