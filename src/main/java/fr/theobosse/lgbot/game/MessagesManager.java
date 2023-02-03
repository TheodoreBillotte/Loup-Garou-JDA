package fr.theobosse.lgbot.game;

import net.dv8tion.jda.api.entities.Message;

public class MessagesManager {

    private Message mainCreationMessage;
    private Message addRoleMessage;
    private Message removeRoleMessage;
    private Message optionsMessage;
    private Message invitesMessage;
    private Message waitingMessage;
    private Message infoMessage;
    private Message votesMessage;
    private Message majorMessage;
    private Message savesMessage;

    public void setMainCreationMessage(Message mainCreationMessage) {
        this.mainCreationMessage = mainCreationMessage;
    }

    public void setAddRoleMessage(Message addRoleMessage) {
        this.addRoleMessage = addRoleMessage;
    }

    public void setRemoveRoleMessage(Message removeRoleMessage) {
        this.removeRoleMessage = removeRoleMessage;
    }

    public void setOptionsMessage(Message optionsMessage) {
        this.optionsMessage = optionsMessage;
    }

    public void setInvitesMessage(Message invitesMessage) {
        this.invitesMessage = invitesMessage;
    }

    public void setWaitingMessage(Message waitingMessage) {
        this.waitingMessage = waitingMessage;
    }

    public void setInfoMessage(Message infoMessage) {
        this.infoMessage = infoMessage;
    }

    public void setVotesMessage(Message votesMessage) {
        this.votesMessage = votesMessage;
    }

    public void setSavesMessage(Message message) {
        this.savesMessage = message;
    }

    public void setMajorMessage(Message majorMessage) {
        this.majorMessage = majorMessage;
    }

    public Message getMainCreationMessage() {
        return mainCreationMessage;
    }

    public Message getAddRoleMessage() {
        return addRoleMessage;
    }

    public Message getRemoveRoleMessage() {
        return removeRoleMessage;
    }

    public Message getOptionsMessage() {
        return optionsMessage;
    }

    public Message getInvitesMessage() {
        return invitesMessage;
    }

    public Message getWaitingMessage() {
        return waitingMessage;
    }

    public Message getInfoMessage() {
        return infoMessage;
    }

    public Message getVotesMessage() {
        return votesMessage;
    }

    public Message getMajorMessage() {
        return majorMessage;
    }

    public Message getSavesMessage() {
        return savesMessage;
    }
}
