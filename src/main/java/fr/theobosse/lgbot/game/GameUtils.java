package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GameUtils {

    private final Game game;
    private Long time = 0L;
    private Boolean day = false;
    private Clan winner = null;
    private Rounds rounds = Rounds.get(0);
    private final List<Role> roles = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();
    private final List<Player> alive = new ArrayList<>();
    private final List<Player> dead = new ArrayList<>();
    private final List<Player> kills = new ArrayList<>();
    private final HashMap<Player, Player> voters = new HashMap<>();
    private final HashMap<Player, Integer> votes = new HashMap<>();
    private final HashMap<Player, List<Player>> couples = new HashMap<>();

    private Player major;


    public GameUtils(Game game) {
        this.game = game;
    }


    public Game getGame() {
        return game;
    }

    public Rounds getRound() {
        return rounds;
    }

    public List<Rounds> getRounds() {
        List<Rounds> rounds = new ArrayList<>();
        for (Role role : roles)
            if (!rounds.contains(role.getRound()))
                rounds.add(role.getRound());

        return rounds;
    }

    public Player getMajor() {
        return major;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Player> getAlive() {
        return alive;
    }

    public List<Player> getDead() {
        return dead;
    }

    public List<Player> getKills() {
        return kills;
    }

    public HashMap<Player, Integer> getVotes() {
        return votes;
    }

    public HashMap<Player, Player> getVoters() {
        return voters;
    }

    public HashMap<Player, List<Player>> getCouples() {
        return couples;
    }

    public List<Player> getCouple(Player cupid) {
        return couples.get(cupid);
    }

    public Long getTime() {
        return time;
    }

    public Boolean getDay() {
        return day;
    }

    public Clan getWinner() {
        return winner;
    }

    public boolean hasWinner() { return winner != null; }



    public void setMajor(Player major) {
        this.major = major;
    }

    public void setRounds(Rounds rounds) {
        this.rounds = rounds;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setDay(Boolean day) {
        this.day = day;
    }

    public void setWinner(Clan winner) {
        this.winner = winner;
    }



    public void addRole(Role role) {
        roles.add(role);
        game.getMessages().updateRolesMessages();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addAlive(Player player) {
        alive.add(player);
    }

    public void addDead(Player player) {
        dead.add(player);
    }

    public void addKills(Player player) {
        kills.add(player);
    }

    public void addCouple(Player cupid, Player love1, Player love2) {
        couples.put(cupid, Arrays.asList(love1, love2));
    }



    public void removeRole(Role role) {
        roles.remove(role);
        game.getMessages().updateRolesMessages();
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public void removeAlive(Player player) {
        alive.remove(player);
    }

    public void removeDead(Player player) {
        dead.remove(player);
    }

    public void removeKills(Player player) {
        kills.remove(player);
    }

    public void removeCouple(Player cupid) {
        couples.remove(cupid);
    }
}
