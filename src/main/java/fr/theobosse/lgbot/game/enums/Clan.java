package fr.theobosse.lgbot.game.enums;

public enum Clan {
    VILLAGE("les Villageois"),
    WEREWOLF("les Loup-Garou"),
    SOLO("le rôle solitaire"),
    LOVE("le Couple");

    Clan(String name) {
        this.name = name;
    }

    private final String name;

    public String getName() {
        return name;
    }
}
