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
    private boolean vote = false;
    private boolean major = false;
    private boolean goSleep = true;

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

                if (isFinish()) {
                    endGame();
                    timer.cancel();
                    timer.purge();
                    return;
                }

                if (playing == 0 && !game.getUtils().getDay())
                    game.getUtils().setTime(0L);
                if (System.currentTimeMillis() < game.getUtils().getTime())
                    return;

                if (goSleep) {
                    onSleep();
                    return;
                }

                if (vote) {
                    onVoteResult();
                    return;
                }

                if (major) {
                    onMajorVoteResult();
                    return;
                }

                if (!game.getUtils().getDay())
                    nightPlay();
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
        HashSet<Clan> clans = new HashSet<>();
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

                if (new HashSet<>(game.getUtils().getAlive()).containsAll(Arrays.asList(cupid, love1, love2))) {
                    game.getUtils().setWinner(Clan.LOVE);
                    return true;
                }
                if (new HashSet<>(game.getUtils().getAlive()).containsAll(Arrays.asList(love1, love2))) {
                    game.getUtils().setWinner(Clan.LOVE);
                    return true;
                }
            }
        }

        if (clans.contains(Clan.SOLO) && players.size() == 1) {
            game.getUtils().setWinner(Clan.SOLO);
            return true;
        }

        if (clans.size() == 1) {
            game.getUtils().setWinner((Clan) clans.toArray()[0]);
            return true;
        }

        return false;
    }

    private void onVote() {
        game.getMessages().sendVotesMessage();
        game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
        vote = true;
    }

    private void onMajorVote() {
        game.getMessages().sendMajorMessage();
        game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getDayTime()));
        major = true;
    }

    private void onSleep() {
        game.getMessages().sendSleepMessage();
        game.getUtils().getPlayers().forEach(p -> {
            try {
                p.getRole().onSleep(p);
            } catch (Exception ignored) {}
        });
        goSleep = false;
    }

    private void onWakeUp() {
        for (Player p : game.getUtils().getKills())
            p.getRole().onNightDeath(p);

        game.getUtils().getDead().addAll(game.getUtils().getKills());
        game.getMessages().sendKillsMessage();
        if (game.getUtils().getKills().contains(game.getUtils().getMajor()))
            onMajorDeath();
        game.getUtils().getKills().forEach(game::kill);
        checkLoveDeath();

        game.getMessages().sendWakeUpMessage();
        game.getUtils().getPlayers().forEach(p -> p.getRole().onWakeUp(p));

        if (isFinish())
            return;

        if (game.getUtils().getMajor() != null)
            onVote();
        else
            onMajorVote();
    }

    private void onVoteResult() {
        Player target = getVoteResult(game.getUtils().getVotes());
        if (target != null) {
            target.getRole().onVoteDeath(target);
            game.getMessages().sendDeathMessage(target);
            game.kill(target);
            if (target.equals(game.getUtils().getMajor()))
                onMajorDeath();
            checkLoveDeath();
        }
        game.getUtils().getVotes().clear();
        game.getUtils().getVoters().clear();

        if (target == null || !target.equals(game.getUtils().getMajor())) {
            game.getUtils().setDay(false);
            goSleep = true;
        }
        vote = false;
    }

    private void onMajorVoteResult() {
        Player target = getVoteResult(game.getUtils().getMajorVotes());
        if (target == null)
            target = game.getUtils().getAlive().get(new Random().nextInt(game.getUtils().getPlayers().size()));
        game.getUtils().setMajor(target);
        game.getMessages().sendElectMessage();
        game.getUtils().getMajorVotes().clear();
        game.getUtils().getVoters().clear();
        major = false;
        onVote();
    }

    private void nightPlay() {
        for (Player p : game.getUtils().getPlayers()) {
            try {
                if (p.getRole().getRound().equals(game.getUtils().getRound()))
                    try {
                        p.getRole().onEndRound(p);
                    } catch (Exception ignored) {}
            } catch (Exception ignored) {}
        }

        game.nextRound();
        if (game.getUtils().getRound() == null) {
            game.getUtils().setDay(true);
            onWakeUp();
        } else {
            for (Player p : game.getUtils().getPlayers()) {
                try {
                    if (p.getRole().getRound().equals(game.getUtils().getRound()) && p.isAlive()) {
                        p.getRole().onPlay(p);
                        playing++;
                    }
                } catch (Exception ignored) {}
            }
            game.getUtils().setTime(System.currentTimeMillis() + (1000L * game.getOptions().getNightTime()));
        }
    }

    public void onMajorDeath() {
        game.getUtils().setTime(Long.MAX_VALUE);
        game.getMessages().sendMajorDeathMessage();
    }

    public void checkLoveDeath() {
        for (Map.Entry<Player, List<Player>> entry : game.getUtils().getCouples().entrySet()) {
            Player cupid = entry.getKey();
            Player love1 = entry.getValue().get(0);
            Player love2 = entry.getValue().get(1);

            if (!game.equals(cupid.game) || (cupid.isAlive() && love1.isAlive() && love2.isAlive()))
                continue;

            if (cupid.isAlive() && !love1.isAlive() && love2.isAlive()) {
                game.getMessages().sendLoveDeathMessage(love2, love1);
                game.getUtils().getCouples().remove(cupid);
                game.kill(love1);
                if (love1.equals(game.getUtils().getMajor()))
                    onMajorDeath();
                break;
            }

            if (cupid.isAlive() && love1.isAlive() && !love2.isAlive()) {
                game.getMessages().sendLoveDeathMessage(love1, love2);
                game.getUtils().getCouples().remove(cupid);
                game.kill(love2);
                if (love2.equals(game.getUtils().getMajor()))
                    onMajorDeath();
                break;
            }
        }
    }

    public Player getVoteResult(HashMap<Player, Integer> votes) {
        long count = 0;
        Player target = null;
        for (Player player : votes.keySet()) {
            long nb = votes.get(player);
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
