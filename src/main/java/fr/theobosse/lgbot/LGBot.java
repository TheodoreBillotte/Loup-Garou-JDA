package fr.theobosse.lgbot;

import fr.theobosse.lgbot.commands.CreateCommand;
import fr.theobosse.lgbot.game.roles.*;
import fr.theobosse.lgbot.listeners.*;
import fr.theobosse.lgbot.utils.CommandManager;
import fr.theobosse.lgbot.utils.JSONLoader;
import fr.theobosse.lgbot.utils.Roles;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LGBot {
    private static final CommandManager cm = new CommandManager();
    public static final HashMap<Guild, HashMap<String, Category>> categories = new HashMap<>();
    public static List<Member> membersList = new ArrayList<>();
    public static JDA jda;

    private static JSONLoader loader;

    public static void main(String[] args) throws Exception {
        loader = new JSONLoader("saves.json");
        String TOKEN;

        try (InputStream tStream = LGBot.class.getClassLoader().getResourceAsStream("TOKEN")) {
            assert tStream != null;
            try (InputStreamReader tStreamReader = new InputStreamReader(tStream);
                 BufferedReader tReader = new BufferedReader(tStreamReader)) {
                TOKEN = tReader.readLine();
            }
        } catch (IOException e) {
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(TOKEN,
                GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS));

        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.playing("/create | Pour jouer !"));

        cm.addCommand("create", new CreateCommand());
        configureMemoryUsage(builder);
        loadEvents(builder);

        JDA jdaB = builder.build();
        jdaB.awaitReady();
        jda = jdaB;
        jda.upsertCommand("create", "Créer une partie de Loup-Garou")
                .addOption(OptionType.STRING, "nom", "Nom de la partie", true)
                .queue();

        loadMembers();
        loadRoles();
    }
    
    public static JDA getJDA() {
        return jda;
    }

    public static List<Member> getMembers() {
        return membersList;
    }

    public static List<Member> getMembers(Guild guild) {
        List<Member> members = new ArrayList<>();
        for (Member m : membersList)
            if (m.getGuild().equals(guild))
                members.add(m);
        return members;
    }

    public static void configureMemoryUsage(JDABuilder builder) {
        builder.disableCache(CacheFlag.ACTIVITY);
        builder.enableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.setMemberCachePolicy(MemberCachePolicy.VOICE.or(MemberCachePolicy.OWNER));
        builder.setChunkingFilter(ChunkingFilter.NONE);
        builder.setLargeThreshold(100);
    }

    private static void loadRoles() {
        Roles.addRole(new WereWolf());
        Roles.addRole(new Villager());
        Roles.addRole(new Hunter());
        Roles.addRole(new LittleGirl());
        Roles.addRole(new Witch());
        Roles.addRole(new Seer());
        Roles.addRole(new Cupidon());
        Roles.addRole(new Crow());
    }

    public static void loadMembers() {
        getJDA().getGuilds().forEach(g -> g.loadMembers().onSuccess(m -> membersList.addAll(m)));
    }

    private static void loadEvents(JDABuilder builder) {
        builder.addEventListeners(cm,
                new AddRoleEvent(),
                new RemoveRoleEvent(),
                new MainCreationEvent(),
                new OptionsEvent(),
                new ChangeDayTimeEvent(),
                new ChangeNightTimeEvent(),
                new InvitesEvent(),
                new ManageInviteEvent(),
                new SetHostEvent(),
                new QuitEvent(),
                new KickEvent(),
                new BanEvent(),
                new JoinEvent(),
                new VoteEvent(),
                new CancelAction(),
                new RoleEvent(),
                new MaxPlayersEvent(),
                new MajorEvent(),
                new SavesEvent(),
                new MajorVoteEvent()
        );
    }

    public static JSONLoader getLoader() {
        return loader;
    }
}
