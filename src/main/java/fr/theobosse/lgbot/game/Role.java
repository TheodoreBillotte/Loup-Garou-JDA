package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

public abstract class Role extends GameActions {

    private String name;
    private String subName;
    private Clan clan;
    private CustomEmoji emoji;
    private Rounds round;
    private String description;

    public Role() {
        LGBot.getJDA().addEventListener(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubName() {
        return subName;
    }

    public void setSubName(String subName) {
        this.subName = subName;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }

    public CustomEmoji getEmoji() {
        return emoji;
    }

    public void setEmoji(CustomEmoji emoji) {
        this.emoji = emoji;
    }

    public Rounds getRound() {
        return round;
    }

    public void setRound(Rounds round) {
        this.round = round;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
