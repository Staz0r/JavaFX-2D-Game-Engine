package game.utils;

import game.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import entity.NPC_Girl;
import entity.NPC_Girl2;
import entity.Entity;

public class NPCUtils {
    private GamePanel gp;
    private boolean initialized = false;

    public NPCUtils(GamePanel gp) {
        this.gp = gp;
    }

    public void initializeNPCs(GraphicsContext gc) {
        if (!initialized) {
            // Initialize NPCs with proper grid alignment
            gp.npc[0] = new NPC_Girl(gp);
            gp.npc[1] = new NPC_Girl2(gp);
            
            // Set initial properties for each NPC
            initializeNPC(gp.npc[0], 29, 19);
            initializeNPC(gp.npc[1], 22, 31);
            
            initialized = true;
        }
    }

    private void initializeNPC(Entity npc, int tileX, int tileY) {
        // Set initial position
        npc.setWorldX(GamePanel.TILE_SIZE * tileX);
        npc.setWorldY(GamePanel.TILE_SIZE * tileY);
        
        // Set initial direction and movement state
        npc.setDirection("down");
        npc.collisionOn = false;
        
        // Debug their initial positions
        if (gp.keyH.isDebugMode) {
            System.out.println("NPC initialized at: " + 
                (npc.getWorldX() / GamePanel.TILE_SIZE) + "," + 
                (npc.getWorldY() / GamePanel.TILE_SIZE));
        }
    }
    
	public void drawNPCs(GraphicsContext gc) {
    	for (int i = 0; i < gp.npc.length; i++) {
    		if (gp.npc[i] != null) {
    			// Ensure NPC is on tile grid
    			int npcX = gp.npc[i].getWorldX();
    			int npcY = gp.npc[i].getWorldY();
    			
    			// Debug info
    			if (gp.keyH.isDebugMode) {
    				System.out.println("NPC " + i + " position: " + 
    					"X=" + npcX + " (" + (npcX / GamePanel.TILE_SIZE) + " tiles), " +
    					"Y=" + npcY + " (" + (npcY / GamePanel.TILE_SIZE) + " tiles)");
    			}
    			
    			gp.npc[i].draw(gc);
    		}
    	}
    }
}