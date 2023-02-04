package fr.theobosse.lgbot.game.enums;

public enum Rounds {

    CUPIDON(1),
    KILLER(2),
    WEREWOLF(3),
    SEER(4),
    WITCH(5),
    FOX(6),
    CROW(7),
    LITTLE_GIRL(8);

    private final int round;

    Rounds(int round) {
        this.round = round;
    }

    public int getRound() {
        return round;
    }

    public static Rounds get(int id) {
        for (Rounds r : Rounds.values())
            if (r.getRound() == id)
                return r;
        return null;
    }
}
