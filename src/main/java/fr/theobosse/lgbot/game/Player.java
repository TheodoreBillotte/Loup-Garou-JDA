package fr.theobosse.lgbot.game;

import net.dv8tion.jda.api.entities.Member;

public class Player {
    Member member;
    Game game;
    Role role;

    public Player(Member member, Game game) {
        this.member = member;
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public Member getMember() {
        return member;
    }

    public Role getRole() {
        return role;
    }


    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAlive() {
        return game.getUtils().getAlive().contains(this);
    }
}
