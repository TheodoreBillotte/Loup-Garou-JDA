package fr.theobosse.lgbot.game;

import fr.theobosse.lgbot.LGBot;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.game.roles.NULL;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;

public class Role {

    private final String name;
    private final String subName;
    private final Clan clan;
    private final CustomEmoji emoji;
    private final Rounds round;
    private final GameActions actions;

    private final String description;

    public Role(String name, String subName, Clan clan, CustomEmoji emote,
                Rounds round, GameActions actions) {
        this.name = name;
        this.subName = subName;
        this.clan = clan;
        this.emoji = emote;
        this.round = round;
        this.actions = actions;
        this.description = "Aucune description";

        if (actions == null)
            actions = new NULL();
        LGBot.getJDA().addEventListener(actions);
    }


    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public Clan getClan() {
        return clan;
    }

    public CustomEmoji getEmoji() {
        return emoji;
    }

    public Rounds getRound() {
        return round;
    }

    public GameActions getActions() {
        return actions;
    }

    public String getDescription() {
        return description;
    }
}
