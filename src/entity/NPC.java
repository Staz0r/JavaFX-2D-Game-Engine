package entity;

import game.GamePanel;
import java.util.Random;

public class NPC extends Entity {
    private String[] dialogues;
    private int currentDialogueIndex;
    private String name;
    
    // Movement related variables
    protected Random random;
    protected int actionLockCounter = 0;
    protected final int ACTION_INTERVAL = 120; // Change action every 120 frames (2 seconds)
    protected String[] directions = {"up", "down", "left", "right"};
    protected boolean isMoving = false;
    private int moveCounter = 0;
    
    public NPC(GamePanel gp) {
        super(gp);
        currentDialogueIndex = 0;
        dialogues = new String[]{"..."};
        random = new Random();
    }
    
    @Override
    public void update() {
        if (GamePanel.gameState == GamePanel.PLAY_STATE) {
            actionLockCounter++;
            
            if (actionLockCounter >= ACTION_INTERVAL) {
                // Reset collision state at the start of each movement check
                collisionOn = false;
                
                // 30% chance to stop if moving
                if (isMoving && random.nextInt(100) < 10) {
                    stopMoving();
                } 
                // 20% chance to start moving if stopped
                else if (!isMoving && random.nextInt(100) < 80) {
                    startMoving();
                }
                
                actionLockCounter = 0;
            }
            
            if (isMoving) {
                // Reset collision flag before checking
                collisionOn = false;
                
                // Get next position before moving
                int nextX = getWorldX();
                int nextY = getWorldY();
                
                switch(direction) {
                    case "up" -> nextY -= getSpeed();
                    case "down" -> nextY += getSpeed();
                    case "left" -> nextX -= getSpeed();
                    case "right" -> nextX += getSpeed();
                }
                
                // Check collisions at the next position
                boolean willCollide = checkCollisions(nextX, nextY);
                
                if (!willCollide) {
                    // Actually move if no collision
                    moveInDirection();
                } else {
                    stopMoving();
                    handleCollision();
                }
            }
            
            // Always update sprite, even when not moving
            updateSprite(isMoving ? getSpeed() : 0);
        }
    }
    
    private boolean checkCollisions(int nextX, int nextY) {
        // Check tile collision first
        gp.collisionH.checkTile(this);
        if (collisionOn) return true;
        
        // Check NPC-to-NPC collision
        collisionOn = gp.collisionH.checkNPCtoNPCCollision(this);
        if (collisionOn) return true;
        
        // Check player collision last
        collisionOn = gp.collisionH.checkPlayerCollision(this);
        return collisionOn;
    }
    
    private void startMoving() {
        isMoving = true;
        direction = directions[random.nextInt(directions.length)];
        moveCounter = 0;
    }
    
    private void stopMoving() {
        isMoving = false;
        moveCounter = 0;
        snapToGrid(); // Ensure we're aligned to the grid when stopping
    }
    
    // Helper method to handle movement
    private void moveInDirection() {
        int oldX = getWorldX();
        int oldY = getWorldY();
        
        // Calculate new position
        switch(direction) {
            case "up" -> setWorldY(oldY - getSpeed());
            case "down" -> setWorldY(oldY + getSpeed());
            case "left" -> setWorldX(oldX - getSpeed());
            case "right" -> setWorldX(oldX + getSpeed());
        }
        
        // Grid alignment check
        moveCounter += getSpeed();
        if (moveCounter >= getBlockSize()) {
            snapToGrid();
        }
    }
    
    // Helper method to handle collision
    private void handleCollision() {
        isMoving = false;
        moveCounter = 0;
        // Choose a new direction that's different from the current one
        String oldDirection = direction;
        do {
            direction = directions[random.nextInt(directions.length)];
        } while (direction.equals(oldDirection));
    }
    
    // Helper method to snap to grid
    @Override
    public void snapToGrid() {
        // Use integer division and multiplication to ensure exact tile alignment
        int tileX = (getWorldX() / GamePanel.TILE_SIZE) * GamePanel.TILE_SIZE;
        int tileY = (getWorldY() / GamePanel.TILE_SIZE) * GamePanel.TILE_SIZE;
        
        // Debug position snapping
        if (gp.keyH.isDebugMode) {
            System.out.println("Snapping NPC to grid: " +
                "(" + (tileX / GamePanel.TILE_SIZE) + "," + 
                (tileY / GamePanel.TILE_SIZE) + ")");
        }
        
        setWorldX(tileX);
        setWorldY(tileY);
        moveCounter = 0;
        isMoving = false;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCurrentDialogue() {
        if (dialogues == null || dialogues.length == 0) {
            return "...";
        }
        return dialogues[currentDialogueIndex];
    }
    
    public void nextDialogue() {
        if (dialogues != null && dialogues.length > 0) {
            currentDialogueIndex = (currentDialogueIndex + 1) % dialogues.length;
        }
    }
    
    public void facePlayerDirection(String playerDirection) {
        switch (playerDirection) {
            case "up":
                direction = "down";
                break;
            case "down":
                direction = "up";
                break;
            case "left":
                direction = "right";
                break;
            case "right":
                direction = "left";
                break;
        }
    }
    
    public void setDialogues(String[] dialogues) {
        this.dialogues = dialogues;
        currentDialogueIndex = 0;
    }
    
    public boolean hasMoreDialogue() {
        return currentDialogueIndex < dialogues.length - 1;
    }
}