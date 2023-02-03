package fr.theobosse.lgbot;

import fr.theobosse.lgbot.commands.CreateCommand;
import fr.theobosse.lgbot.game.Role;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.game.roles.*;
import fr.theobosse.lgbot.listeners.*;
import fr.theobosse.lgbot.utils.CommandManager;
import fr.theobosse.lgbot.utils.Emotes;
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
        Roles.addRole(new Role("Loup-Garou", "LG", Clan.WEREWOLF, Emotes.getEmote("werewolf"), Rounds.WEREWOLF, new WereWolf()));
        Roles.addRole(new Role("Villageois", "Villageois", Clan.VILLAGE, Emotes.getEmote("villager"), null, new Villager()));
        Roles.addRole(new Role("Chasseur", "Chasseur", Clan.VILLAGE, Emotes.getEmote("hunter"), null, new Hunter()));
        Roles.addRole(new Role("Petite Fille", "PF", Clan.VILLAGE, Emotes.getEmote("little_girl"), Rounds.WEREWOLF, new LittleGirl()));
        Roles.addRole(new Role("Sorcière", "Sorcière", Clan.VILLAGE, Emotes.getEmote("witch"), Rounds.WITCH, new Witch()));
        Roles.addRole(new Role("Corbeau", "Corbeau", Clan.VILLAGE, Emotes.getEmote("crow"), Rounds.CROW, new Crow()));
        Roles.addRole(new Role("Voyante", "Voyante", Clan.VILLAGE, Emotes.getEmote("seer"), Rounds.SEER, new Seer()));
         Roles.addRole(new Role("Cupidon", "Cupidon", Clan.VILLAGE, Emotes.getEmote("cupid"), Rounds.CUPID, new Cupidon()));
        // Roles.addRole(new Role("Renard", "Renard", Clan.VILLAGE, Emotes.getEmote("fox"), Rounds.FOX, null));
        // Roles.addRole(new Role("Loup-Garou Blanc", "LGB", Clan.SOLO, Emotes.getEmote("white_werewolf"), Rounds.WEREWOLF, null));
        // Roles.addRole(new Role("Assassin", "Assassin", Clan.SOLO, Emotes.getEmote("killer"), Rounds.KILLER, null));
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
                new SavesEvent()
        );
    }

    public static JSONLoader getLoader() {
        return loader;
    }
}
