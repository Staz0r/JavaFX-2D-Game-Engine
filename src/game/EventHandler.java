package game;

import entity.Entity;
import entity.NPC;
import object.SuperObject;

public class EventHandler {
    GamePanel gp;
    private boolean eventInProgress;

    public EventHandler(GamePanel gp) {
        this.gp = gp;
        this.eventInProgress = false;
    }

    public void checkEvent() {
        if (eventInProgress) {
            return;
        }

        if (GamePanel.gameState == GamePanel.PLAY_STATE) {
            checkNPCInteraction();
            checkObjectInteraction();
        }
    }

    private void checkNPCInteraction() {
        if (!gp.keyH.interactPressed) {
            return;  // Exit early if F key isn't pressed
        }
        
        for (Entity npc : gp.npc) {
            if (npc != null && isNPCInFacingDirection(gp.player, npc)) {
                startInteraction(npc);
                gp.keyH.interactPressed = false;
                return;
            }
        }
        
        // Then check for objects
        if (gp.objUtils != null) {
            for (SuperObject obj : gp.objUtils.objects) {
                if (obj != null && !obj.isCollected() && isObjectInFacingDirection(gp.player, obj)) {
                    startObjectInteraction(obj);
                    gp.keyH.interactPressed = false;
                    return;
                }
            }
        }
    }

    private void checkObjectInteraction() {
        if (!gp.keyH.interactPressed) {
            return;
        }
        
        if (gp.objUtils != null) {
            for (SuperObject obj : gp.objUtils.objects) {
                if (obj != null && !obj.isCollected() && 
                    isObjectInFacingDirection(gp.player, obj)) {
                    startObjectInteraction(obj);
                    gp.keyH.interactPressed = false;
                    return;
                }
            }
        }
    }

    private void startInteraction(Entity entity) {
        if (entity instanceof NPC) {
            NPC npc = (NPC) entity;
            eventInProgress = true;
            GamePanel.gameState = GamePanel.DIALOGUE_STATE;
            
            // Make NPC face opposite direction of player
            npc.facePlayerDirection(gp.player.getDirection());
            npc.updateSprite(0);
            
            gp.dialogueH.speak(npc);
            gp.debugUtils.logEvent("Started dialogue with " + npc.getName());
        }
    }

    private void startObjectInteraction(SuperObject obj) {
        eventInProgress = true;
        GamePanel.gameState = GamePanel.DIALOGUE_STATE;
        gp.dialogueH.speakObject(obj);
    }

    public void endInteraction() {
        eventInProgress = false;
        GamePanel.gameState = GamePanel.PLAY_STATE;
    }

    // Getters and setters
    public boolean isEventInProgress() {
        return eventInProgress;
    }

    public void setEventInProgress(boolean eventInProgress) {
        this.eventInProgress = eventInProgress;
    }

    // Helpers for checking NPC interaction
    public static boolean isWithinInteractionRange(int playerCol, int playerRow, int npcCol, int npcRow) {
        return (playerCol == npcCol && Math.abs(playerRow - npcRow) == 1) ||  // Above or below
                (playerRow == npcRow && Math.abs(playerCol - npcCol) == 1);    // Left or right
    }

    public static boolean isNPCInFacingDirection(Entity player, Entity npc) {
        int playerCol = player.getWorldX() / GamePanel.TILE_SIZE;
        int playerRow = player.getWorldY() / GamePanel.TILE_SIZE;
        int npcCol = npc.getWorldX() / GamePanel.TILE_SIZE;
        int npcRow = npc.getWorldY() / GamePanel.TILE_SIZE;
        
        switch (player.getDirection()) {
            case "up":
                return (playerCol == npcCol && playerRow - 1 == npcRow);
            case "down":
                return (playerCol == npcCol && playerRow + 1 == npcRow);
            case "left":
                return (playerCol - 1 == npcCol && playerRow == npcRow);
            case "right":
                return (playerCol + 1 == npcCol && playerRow == npcRow);
            default:
                return false;
        }
    }

    public static boolean isObjectInFacingDirection(Entity player, SuperObject obj) {
        int playerCol = player.getWorldX() / GamePanel.TILE_SIZE;
        int playerRow = player.getWorldY() / GamePanel.TILE_SIZE;
        int objCol = obj.getWorldX() / GamePanel.TILE_SIZE;
        int objRow = obj.getWorldY() / GamePanel.TILE_SIZE;
        
        switch (player.getDirection()) {
            case "up":
                return (playerCol == objCol && playerRow - 1 == objRow);
            case "down":
                return (playerCol == objCol && playerRow + 1 == objRow);
            case "left":
                return (playerCol - 1 == objCol && playerRow == objRow);
            case "right":
                return (playerCol + 1 == objCol && playerRow == objRow);
            default:
                return false;
        }
    }
}