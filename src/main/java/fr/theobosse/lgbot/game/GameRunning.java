package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.GameState;
import fr.theobosse.lgbot.utils.GuildManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.*;

public class GameRunning {
    private final Game game;

    public GameRunning(Game game) {
        this.game = game;
    }

    public boolean starting() {
        if (game.getUtils().getPlayers().size() != game.getUtils().getRoles().size())
            return false;

        game.getMessages().updateInfoMessages();
        game.setState(GameState.STARTING);
        game.setStartTime(15);
        final int[] i = {15};
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                game.setStartTime(i[0]);

                if ((i[0] >= 0 && i[0] <= 3) || i[0] == 5 || i[0] == 10 || i[0] == 15) {
                    game.getMessages().updateWaitingMessages();
                    game.getMessages().updateInfoMessages();
                    game.getMessages().updateMainMessage();
                }


                if (game.getState().equals(GameState.WAITING)) {
                    game.getMessages().updateWaitingMessages();
                    game.getMessages().updateInfoMessages();
                    timer.cancel();
                    timer.purge();
                }

                if (i[0] == 0 && game.getState().equals(GameState.STARTING)) {
                    game.getMessagesManager().getInfoMessage().delete().complete();
                    game.getChannelsManager().getWaitingChannel().delete().complete();
                    game.getChannelsManager().getCreationChannel().delete().complete();
                    start();
                    timer.cancel();
                    timer.purge();
                }
                i[0]--;
            }
        }, 1000, 1000);
        return true;
    }

    public void start() {
        game.setState(GameState.RUNNING);
        Timer timer = new Timer();
        Random random = new Random();
        List<Player> players = new ArrayList<>(game.getUtils().getPlayers());
        List<Role> roles = new ArrayList<>(game.getUtils().getRoles());
        Collections.shuffle(roles);
        game.getUtils().getAlive().addAll(game.getUtils().getPlayers());
        for (Role r : roles) {
            Player p = players.get(random.nextInt(players.size()));
            players.remove(p);
            p.setRole(r);
        }

        TextChannel vChannel = GuildManager.createVillageChannel(game);
        TextChannel lChannel = GuildManager.createWereWolfChannel(game);
        VoiceChannel voiceChannel = GuildManager.createVoiceChannel(game);

        game.getChannelsManager().setVillageChannel(vChannel);
        game.getChannelsManager().setWerewolfChannel(lChannel);
        game.getChannelsManager().setVoiceChannel(voiceChannel);

        final boolean[] start = {true};
        final boolean[] wakeup = {false};
        final boolean[] goSleep = {true};
        final boolean[] hasVoted = {false};
        final List<Player> hasWakeUp = new ArrayList<>();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (start[0]) {
                    game.getMessages().sendStartMessage();
                    start[0] = false;
                }

                if (isFinish() && game.getUtils().getTime() < System.currentTimeMillis()) {
                    endGame();
                    timer.cancel();
                    timer.purge();
                    return;
                }

                if (goSleep[0]) {
                    game.getMessages().sendSleepMessage();
                    game.getUtils().getPlayers().forEach(p -> {
                                try {
                                    p.getRole().getActions().onSleep(p);
                                } catch (Exception ignored) {}
                            });
                    goSleep[0] = false;
                }

                if (System.currentTimeMillis() > game.getUtils().getTime() && !game.getUtils().getDay()) {
                    game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getNightTime()));
                    for (Player p : game.getUtils().getPlayers()) {
                        try {
                            if (p.getRole().getRound().equals(game.getUtils().getRound()))
                                try {
                                    p.getRole().getActions().onEndRound(p);
                                } catch (Exception ignored) {}
                        } catch (Exception ignored) {}
                    }

                    game.nextRound();

                    if (game.getUtils().getRound() == null) {
                        game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
                        game.getUtils().setDay(true);
                    } else {
                        int count = 0;
                        for (Player p : game.getUtils().getPlayers()) {
                            try {
                                if (p.getRole().getRound().equals(game.getUtils().getRound()) && p.isAlive()) {
                                    p.getRole().getActions().onPlay(p);
                                    count++;
                                }
                            } catch (Exception ignored) {}
                        }
                        if (count == 0)
                            game.getUtils().setTime(0L);
                    }
                }

                if (game.getUtils().getDay() && !hasVoted[0] && !wakeup[0] && !isFinish() && game.getUtils().getTime() < System.currentTimeMillis()) {
                    for (Player p : game.getUtils().getKills()) {
                        if (!hasWakeUp.contains(p)) {
                            p.getRole().getActions().onNightDeath(p);
                            hasWakeUp.add(p);
                        }
                    }

                    if (hasWakeUp.size() == game.getUtils().getKills().size()) {
                        game.getUtils().getDead().addAll(game.getUtils().getKills());
                        game.getMessages().sendKillsMessage();
                        game.getUtils().getKills().forEach(game::kill);
                        hasVoted[0] = true;
                        wakeup[0] = true;
                    }
                }

                if (wakeup[0] && !isFinish()) {
                    game.getMessages().sendWakeUpMessage();
                    game.getUtils().getPlayers().forEach(p ->
                            p.getRole().getActions().onWakeUp(p));
                    game.getMessages().sendVotesMessage();
                    game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
                    wakeup[0] = false;
                }

                if (System.currentTimeMillis() > game.getUtils().getTime() && game.getUtils().getDay()) {
                    long count = 0;
                    Player target = null;
                    for (Player player : game.getUtils().getVotes().keySet()) {
                        long nb = game.getUtils().getVotes().get(player);
                        if (nb > count) {
                            count = nb;
                            target = player;
                        } else if (nb == count)
                            if (random.nextInt(2) == 0)
                                target = player;
                    }

                    if (target != null) {
                        target.getRole().getActions().onVoteDeath(target);
                        game.getMessages().sendDeathMessage(target);
                        game.kill(target);
                    }
                    game.getUtils().getVotes().clear();

                    game.getUtils().setDay(false);
                    hasVoted[0] = false;
                    goSleep[0] = true;
                }
            }
        }, 50, 50);
    }

    public void endGame() {
        game.getMessages().sendEndMessage();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                game.getChannelsManager().getVillageChannel().delete().complete();
                game.getChannelsManager().getWerewolfChannel().delete().complete();
                game.getChannelsManager().getVoiceChannel().delete().complete();
                game.getUtils().getPlayers().forEach(GamesInfo::removePlayer);
                GamesInfo.removeGame(game);
            }
        }, 30000);
    }

    public boolean isFinish() {
        List<Clan> clans = new ArrayList<>();
        List<Player> players = game.getUtils().getAlive();
        if (players.size() == 0) {
            game.getUtils().setWinner(null);
            return true;
        }

        players.forEach(p -> {
            try {
                clans.add(p.getRole().getClan());
            } catch (Exception ignored) {}
        });

        if (players.size() == 2 || players.size() == 3) {
            for (Map.Entry<Player, List<Player>> entry : game.getUtils().getCouples().entrySet()) {
                Player cupid = entry.getKey();
                Player love1 = entry.getValue().get(0);
                Player love2 = entry.getValue().get(1);

                if (players.size() == 2)
                    if (new HashSet<>(game.getUtils().getAlive()).containsAll(Arrays.asList(cupid, love1, love2))) {
                        game.getUtils().setWinner(Clan.LOVE);
                        return true;
                    }
                    else if (new HashSet<>(game.getUtils().getAlive()).containsAll(Arrays.asList(love1, love2))) {
                        game.getUtils().setWinner(Clan.LOVE);
                        return true;
                    }
            }
        }

        if (clans.contains(Clan.SOLO) && players.size() < 1) {
            game.getUtils().setWinner(Clan.SOLO);
            return true;
        }

        if (clans.size() == 1) {
            game.getUtils().setWinner(clans.get(0));
            return true;
        }

        return false;
    }
}
