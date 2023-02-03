package fr.theobosse.lgbot.game.roles;

import fr.theobosse.lgbot.game.enums.Clan;
import fr.theobosse.lgbot.game.enums.Rounds;
import fr.theobosse.lgbot.utils.Emotes;

public class WhiteWerewolf extends WereWolf {

    public WhiteWerewolf() {
        setName("Loup-Garou Blanc");
        setSubName("LGB");
        setClan(Clan.SOLO);
        setEmoji(Emotes.getEmote("white_werewolf"));
        setRound(Rounds.WEREWOLF);

        setDescription("Son objectif est de terminer SEUL la partie. Les autres Loups-Garous croient qu'il est " +
                "un loup normal, mais en réalité il doit tous les trahir !");
    }

}
