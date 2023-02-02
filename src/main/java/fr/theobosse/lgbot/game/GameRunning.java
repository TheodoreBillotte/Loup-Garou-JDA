package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.GameState;
import fr.theobosse.lgbot.utils.GuildManager;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.*;

public class GameRunning {
    private final Game game;
    private int start_timer;
    private boolean start = true;
    private boolean wakeup = false;
    private boolean goSleep = true;
    private boolean hasVoted = false;

    private boolean messageSent = false;

    private int playing = 0;

    public GameRunning(Game game) {
        this.game = game;
    }

    public boolean starting() {
        if (game.getUtils().getPlayers().size() != game.getUtils().getRoles().size())
            return false;

        game.getMessages().updateInfoMessages();
        game.setState(GameState.STARTING);
        game.setStartTime(15);
        start_timer = 15;
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                game.setStartTime(start_timer);
                if (start_timer == 15)
                    game.getMessages().updateInfoMessages();

                game.getMessages().updateWaitingMessages();
                game.getMessages().updateMainMessage();

                if (game.getState().equals(GameState.WAITING)) {
                    game.getMessages().updateWaitingMessages();
                    game.getMessages().updateInfoMessages();
                    timer.cancel();
                    timer.purge();
                }

                if (start_timer == 0 && game.getState().equals(GameState.STARTING)) {
                    game.getMessagesManager().getInfoMessage().delete().complete();
                    game.getChannelsManager().getWaitingChannel().delete().complete();
                    game.getChannelsManager().getCreationChannel().delete().complete();
                    start();
                    timer.cancel();
                    timer.purge();
                }
                start_timer--;
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

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (start) {
                    game.getMessages().sendStartMessage();
                    start = false;
                }

                if (wakeup && !isFinish() && game.getUtils().getMajor() != null) {
                    if (!messageSent) {
                        game.getMessages().sendWakeUpMessage();
                        game.getUtils().getPlayers().forEach(p -> p.getRole().getActions().onWakeUp(p));
                    }

                    game.getMessages().sendVotesMessage();
                    game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
                    wakeup = false;
                }

                if (wakeup && !isFinish() && game.getUtils().getMajor() == null) {
                    game.getMessages().sendWakeUpMessage();
                    game.getUtils().getPlayers().forEach(p -> p.getRole().getActions().onWakeUp(p));
                    game.getMessages().sendMajorMessage();
                    game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
                    messageSent = true;
                    wakeup = false;
                }

                if (playing == 0 && !game.getUtils().getDay())
                    game.getUtils().setTime(0L);
                if (System.currentTimeMillis() < game.getUtils().getTime())
                    return;

                if (isFinish()) {
                    endGame();
                    timer.cancel();
                    timer.purge();
                    return;
                }

                if (goSleep) {
                    game.getMessages().sendSleepMessage();
                    game.getUtils().getPlayers().forEach(p -> {
                        try {
                            p.getRole().getActions().onSleep(p);
                        } catch (Exception ignored) {}
                    });
                    goSleep = false;
                    return;
                }

                if (!game.getUtils().getDay()) {
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
                        game.getUtils().setDay(true);
                    } else {
                        for (Player p : game.getUtils().getPlayers()) {
                            try {
                                if (p.getRole().getRound().equals(game.getUtils().getRound()) && p.isAlive()) {
                                    p.getRole().getActions().onPlay(p);
                                    playing++;
                                }
                            } catch (Exception ignored) {}
                        }
                        game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getNightTime()));
                    }
                    return;
                }

                if (game.getUtils().getDay() && !hasVoted && !wakeup && !isFinish()) {
                    for (Player p : game.getUtils().getKills())
                        p.getRole().getActions().onNightDeath(p);

                    game.getUtils().getDead().addAll(game.getUtils().getKills());
                    game.getMessages().sendKillsMessage();
                    if (game.getUtils().getKills().contains(game.getUtils().getMajor()))
                        onMajorDeath();
                    game.getUtils().getKills().forEach(game::kill);
                    hasVoted = true;
                    wakeup = true;
                    return;
                }

                if (game.getUtils().getDay() && game.getUtils().getMajor() != null) {
                    Player target = getVoteResult();
                    if (target != null) {
                        target.getRole().getActions().onVoteDeath(target);
                        game.getMessages().sendDeathMessage(target);
                        game.kill(target);
                        if (target.equals(game.getUtils().getMajor()))
                            onMajorDeath();
                    }
                    game.getUtils().getVotes().clear();
                    game.getUtils().getVoters().clear();

                    if (target == null || !target.equals(game.getUtils().getMajor())) {
                        game.getUtils().setDay(false);
                        hasVoted = false;
                        goSleep = true;
                    }
                    return;
                }

                if (game.getUtils().getDay() && game.getUtils().getMajor() == null) {
                    Player target = getVoteResult();
                    if (target == null)
                        target = game.getUtils().getAlive().get(random.nextInt(game.getUtils().getPlayers().size()));
                    game.getUtils().setMajor(target);
                    game.getMessages().sendElectMessage();
                    game.getUtils().getVotes().clear();
                    game.getUtils().getVoters().clear();
                    wakeup = true;
                }
            }
        }, 500, 500);
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

    public void onMajorDeath() {
        game.getUtils().setTime(Long.MAX_VALUE);
        game.getMessages().sendMajorDeathMessage();
    }

    public Player getVoteResult() {
        long count = 0;
        Player target = null;
        for (Player player : game.getUtils().getVotes().keySet()) {
            long nb = game.getUtils().getVotes().get(player);
            if (nb > count) {
                count = nb;
                target = player;
            } else if (nb == count)
                if (new Random().nextInt(2) == 0)
                    target = player;
        }
        return target;
    }

    public int getPlaying() {
        return playing;
    }

    public void setPlaying(int playing) {
        this.playing = playing;
    }

    public void played() {
        playing--;
    }

}
