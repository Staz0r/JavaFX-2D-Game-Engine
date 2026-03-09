package game;

import entity.NPC;
import object.SuperObject;

public class DialogueHandler {
    GamePanel gp;
    private NPC currentSpeaker;
    private SuperObject currentObject;
    
    public DialogueHandler(GamePanel gp) {
        this.gp = gp;
    }
    
    public void speak(NPC npc) {
        currentSpeaker = npc;
        gp.debugUtils.logEvent("Dialogue started with: " + npc.getName());
    }
    
    public NPC getCurrentSpeaker() {
        return currentSpeaker;
    }
    
    public void handleDialogueInput() {
        if (gp.keyH.interactPressed) {
            if (currentSpeaker != null) {
                handleNPCDialogue();
            } else if (currentObject != null) {
                handleObjectDialogue();
            }
            gp.keyH.interactPressed = false;
        }
    }
    
    private void handleNPCDialogue() {
        if (currentSpeaker.hasMoreDialogue()) {
            currentSpeaker.nextDialogue();
            gp.debugUtils.logEvent("Next dialogue line");
        } else {
            endDialogue();
        }
    }
    
    public void speakObject(SuperObject obj) {
        currentObject = obj;
        currentSpeaker = null;
        gp.debugUtils.logEvent("Dialogue started with object: " + obj.name);
    }
    
    private void handleObjectDialogue() {
        if (currentObject.hasMoreDialogue()) {
            currentObject.nextDialogue();
            gp.debugUtils.logEvent("Next object dialogue line");
        } else {
            currentObject.setCollected(true);
            gp.debugUtils.logEvent(currentObject.getName() + " was collected");
            endDialogue();
        }
    }
    
    public String getCurrentDialogue() {
        if (currentSpeaker != null) {
            return currentSpeaker.getCurrentDialogue();
        } else if (currentObject != null) {
            return currentObject.getCurrentDialogue();
        }
        return "...";
    }
    
    public boolean hasMoreDialogue() {
        if (currentSpeaker != null) {
            return currentSpeaker.hasMoreDialogue();
        } else if (currentObject != null) {
            return currentObject.hasMoreDialogue();
        }
        return false;
    }
    
    public void endDialogue() {
    
        gp.debugUtils.logEvent("Dialogue ended");
        currentSpeaker = null;
        currentObject = null;
        gp.eventH.endInteraction();
    }
    
    public SuperObject getCurrentObject() {
        return currentObject;
    }
}