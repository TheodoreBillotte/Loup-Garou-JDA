package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.enums.Rounds;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class GamesInfo {

    private static final List<Game> games = new ArrayList<>();
    private static final List<Player> players = new ArrayList<>();


    public static Member getMember(Guild guild, String tag) {
        LGBot.loadMembers();
        for (Member m : LGBot.getMembers(guild)) {
            User u = m.getUser();
            if ((m.getNickname() != null && m.getNickname().equals(tag)) ||
                    m.getEffectiveName().equals(tag) ||
                    (u.getName() + u.getDiscriminator()).equals(tag) ||
                    m.getId().equals(tag)) {
                return m;
            }
        }
        return null;
    }

    public static Player getPlayer(Member member) {
        for (Player player : players)
            if (player.getMember().equals(member))
                return player;
        return null;
    }

    public static Player getPlayer(User user) {
        for (Player player : players)
            if (player.getMember().getUser().equals(user))
                return player;
        return null;
    }

    public static Player getPlayer(Game game, String tag) {
        for (Player p : game.getUtils().getPlayers()) {
            Member member = p.getMember();
            User user = member.getUser();
            if ((member.getNickname() != null && member.getNickname().equals(tag)) ||
                    member.getEffectiveName().equals(tag) ||
                    (user.getName() + user.getDiscriminator()).equals(tag) ||
                    member.getId().equals(tag))
                return p;
        }
        return null;
    }

    public static Player getPlayer(String tag) {
        for (Player p : players) {
            Member member = p.getMember();
            User user = member.getUser();
            if ((member.getNickname() != null && member.getNickname().equals(tag)) ||
                    member.getEffectiveName().equals(tag) ||
                    (user.getName() + user.getDiscriminator()).equals(tag) ||
                    member.getId().equals(tag))
                return p;
        }
        return null;
    }

    public static Player getPlayer(Message msg, Game game) {
        User targetUser = null;

        try {
            targetUser = msg.getMentions().getUsers().get(0);
        } catch (Exception ignored) {}
        Player target;

        if (targetUser == null) {
            String tag = msg.getContentDisplay();
            target = GamesInfo.getPlayer(game, tag);
        } else
            target = GamesInfo.getPlayer(targetUser);
        return target;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Game getGame(Member member) {
        return Objects.requireNonNull(getPlayer(member)).getGame();
    }

    public static Game getGame(String messageID) {
        for (Game game : games) {
            if (game.getMessagesManager().getInfoMessage().getId().equals(messageID))
                return game;
        }
        return null;
    }

    public static Role getRole(Member member) {
        return Objects.requireNonNull(getPlayer(member)).getRole();
    }

    public static List<Game> getGames() {
        return games;
    }

    public static Game getGameByName(String name) {
        for (Game game : games) {
            if (game.getName().equalsIgnoreCase(name))
                return game;
        }
        return null;
    }

    public static List<Player> getPlayers(Game game, Rounds round) {
        return game.getUtils().getPlayers().stream().filter(player -> player.getRole().getRound().equals(round)).collect(Collectors.toList());
    }



    public static void addGame(Game game) {
        games.add(game);
    }

    public static void addPlayer(Player player) {
        players.add(player);
    }

    public static void addPlayer(Member member, Game game) {
        players.add(new Player(member, game));
    }



    public static void removeGame(Game game) {
        games.remove(game);
    }

    public static void removePlayer(Player player) {
        players.remove(player);
    }

}
