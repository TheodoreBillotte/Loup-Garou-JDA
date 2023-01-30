package fr.theobosse.lgbot.game.enums;

public enum Clan {
    VILLAGE("Villageoi", "Villageois"),
    WEREWOLF("Loup-Garou", "Loups-Garou"),
    SOLO("Seul", "Error"),
    LOVE("Error", "Couple");

    Clan(String simpleName, String anyName) {
        this.simpleName = simpleName;
        this.anyName = anyName;
    }

    private final String simpleName;
    private final String anyName;

    public String getSimpleName() {
        return simpleName;
    }

    public String getAnyName() {
        return anyName;
    }
}
