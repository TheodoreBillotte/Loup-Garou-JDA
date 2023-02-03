package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.Role;
import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.utils.Emotes;

public class Villager extends Role {

    public Villager() {
        setName("Villageois");
        setSubName("Villageois");
        setClan(Clan.VILLAGE);
        setEmoji(Emotes.getEmote("villager"));
        setRound(null);
        setDescription("Son objectif est d'éliminer tous les Loups-Garous. Il ne dispose d'aucun pouvoir particulier" +
                " : uniquement sa perspicacité et sa force de persuasion.");
    }

}
