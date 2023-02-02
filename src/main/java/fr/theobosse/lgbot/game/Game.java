package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.game.enums.GameState;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.GuildManager;
import fr.theobosse.lgbot.utils.Messages;
import fr.theobosse.lgbot.utils.Roles;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;
import java.util.Objects;

public class Game {

    private Member host;
    private Player pHost;
    private final String name;
    private final Messages messages;
    private final MessagesManager messagesManager;
    private final ChannelsManager channelsManager;
    private final GameRunning gameRunning;
    private final GameUtils utils;
    private final Options options;
    private GameState state;
    private Integer startTime;

    public Game(Member host, String name) {
        pHost = new Player(host, this);
        messages = new Messages(this);
        this.host = host;
        this.name = name;

        messagesManager = new MessagesManager();
        channelsManager = new ChannelsManager();
        gameRunning = new GameRunning(this);
        utils = new GameUtils(this);
        options = new Options();
        state = GameState.WAITING;

        GamesInfo.addGame(this);
        GamesInfo.addPlayer(pHost);
        GuildManager.createInfoChannel(getGuild());
        GuildManager.createCategory(getGuild(), "GameCreation");
        GuildManager.createCategory(getGuild(), "Salles d'attente");
        GuildManager.createCategory(getGuild(), "Games");

        TextChannel channel = GuildManager.createGameCreationChannel(this);
        TextChannel wChannel = GuildManager.createWaitingChannel(this);

        utils.addPlayer(pHost);
        channelsManager.setCreationChannel(channel);
        channelsManager.setWaitingChannel(wChannel);
        messages.sendMainMessage();
        messages.sendWaitingMessage();
        messages.sendInfoMessage();


        // TEST CONFIG
        options.setNightTime(10);
        options.setInvite(false);
        utils.addRole(Roles.getRoleByName("Villageois"));
        utils.addRole(Roles.getRoleByName("Loup-Garou"));
    }

    public void setHost(Player host) {
        Member oldHost = this.host;
        this.host = host.member;
        this.pHost = host;
        GuildManager.modifyGameCreationChannel(this, oldHost);
        this.messages.updateMainMessage();
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }



    public boolean canJoin(Member member) {
        return utils.getPlayers().size() < options.getMaxPlayers() &&
                state.equals(GameState.WAITING) &&
                !options.isBanned(member) &&
                (!options.gameIsOnInvite() || options.isInvited(member));
    }

    public String getName() {
        return name;
    }

    public Member getHost() {
        return host;
    }

    public Options getOptions() {
        return options;
    }

    public Guild getGuild() {
        return host.getGuild();
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public Messages getMessages() {
        return messages;
    }

    public ChannelsManager getChannelsManager() {
        return channelsManager;
    }

    public GameUtils getUtils() {
        return utils;
    }

    public GameState getState() {
        return state;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public GameRunning getGameRunning() {
        return gameRunning;
    }



    public void join(Member member) {
        GamesInfo.addPlayer(member, this);
        utils.addPlayer(p(member));
        GuildManager.addWaitingPermissions(this, member);
        messages.updateMainMessage();
        messages.updateInfoMessages();
    }

    public void quit(Member member) {
        utils.removePlayer(p(member));
        GamesInfo.removePlayer(p(member));
        GuildManager.removeWaitingPermissions(this, member);
        messages.updateMainMessage();
        messages.updateInfoMessages();
    }

    public void ban(Member member) {
        options.addBanned(member);
        if (utils.getPlayers().contains(p(member)))
            quit(member);
    }

    public void kill(Player player) {
        utils.getDead().add(player);
        utils.getAlive().remove(player);
        utils.getRoles().remove(player.getRole());

        if (options.isDeadAreMuted()) {
            try {
                Objects.requireNonNull(channelsManager.getVoiceChannel().getPermissionOverride(player.getMember())).getAllowed().remove(Permission.VOICE_SPEAK);
                Objects.requireNonNull(channelsManager.getVoiceChannel().getPermissionOverride(player.getMember())).getDenied().add(Permission.VOICE_SPEAK);
            } catch (Exception ignored) {}
        }

        try {
            Objects.requireNonNull(channelsManager.getVillageChannel().getPermissionOverride(player.getMember())).getAllowed().remove(Permission.MESSAGE_SEND);
            Objects.requireNonNull(channelsManager.getVillageChannel().getPermissionOverride(player.getMember())).getDenied().add(Permission.MESSAGE_SEND);
        } catch (Exception ignored) {}
        try {
            Objects.requireNonNull(channelsManager.getWerewolfChannel().getPermissionOverride(player.getMember())).getAllowed().remove(Permission.MESSAGE_SEND);
            Objects.requireNonNull(channelsManager.getWerewolfChannel().getPermissionOverride(player.getMember())).getDenied().add(Permission.MESSAGE_SEND);
        } catch (Exception ignored) {}
    }

    public void nextRound() {
        List<Rounds> rounds = getUtils().getRounds();
        if (rounds.indexOf(getUtils().getRound()) + 1 < rounds.size())
            getUtils().setRounds(rounds.get(rounds.indexOf(getUtils().getRound()) + 1));
        else
            if (getUtils().getRound() == null)
                getUtils().setRounds(rounds.get(0));
            else
                getUtils().setRounds(null);
        gameRunning.setPlaying(0);
    }


    private Player p(Member member) {
        return GamesInfo.getPlayer(member);
    }
}
