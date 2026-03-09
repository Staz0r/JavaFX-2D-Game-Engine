package game.utils;

import game.CollisionHandler;
import game.EventHandler;
import game.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tile.Floor;

public class DebugUI {
    
    private GamePanel gp;
    private final Font DEBUG_FONT = Font.font("Arial", FontWeight.BOLD, 16);
    private final Font COORDINATE_FONT = Font.font("Arial", 10);
    
    private static final int MARGIN_LEFT = 10;
    private static final int LINE_HEIGHT = 20;
    private static final int FIRST_LINE_Y = 25;
    private static int LINE = 0;
    
    
    public DebugUI(GamePanel gp) {
    	
        this.gp = gp;
    }
    
    public void drawGrid(GraphicsContext gc) {
    	
        double originalLineWidth = gc.getLineWidth();
        Paint originalStroke = gc.getStroke();
        Font originalFont = gc.getFont();
        
        VisibleArea area = new VisibleArea();
        MapUtils.getVisibleArea(gp, area);
        
        int leftBound = gp.camera.getScreenX(0, gp.player);
        int rightBound = gp.camera.getScreenX(GamePanel.MAX_WORLD_COL * GamePanel.TILE_SIZE, gp.player);
        int topBound = gp.camera.getScreenY(0, gp.player);
        int bottomBound = gp.camera.getScreenY(GamePanel.MAX_WORLD_ROW * GamePanel.TILE_SIZE, gp.player);
        
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(1);
        gc.setGlobalAlpha(0.3);
        
        // Draw vertical lines
        for (int col = area.startCol; col <= area.endCol; col++) {
            int worldX = col * GamePanel.TILE_SIZE;
            int screenX = gp.camera.getScreenX(worldX, gp.player);
            gc.strokeLine(screenX, topBound, screenX, bottomBound);
        }
        
        // Draw horizontal lines
        for (int row = area.startRow; row <= area.endRow; row++) {
            int worldY = row * GamePanel.TILE_SIZE;
            int screenY = gp.camera.getScreenY(worldY, gp.player);
            gc.strokeLine(leftBound, screenY, rightBound, screenY);
        }

        gc.setFont(COORDINATE_FONT);
        gc.setGlobalAlpha(0.8);
        gc.setFill(Color.WHITE);
        
        for (int row = area.startRow; row < area.endRow; row++) {
            for (int col = area.startCol; col < area.endCol; col++) {
                int worldX = col * GamePanel.TILE_SIZE;
                int worldY = row * GamePanel.TILE_SIZE;
                
                if (MapUtils.isVisibleOnScreen(worldX, worldY, gp)) {
                    int screenX = gp.camera.getScreenX(worldX, gp.player);
                    int screenY = gp.camera.getScreenY(worldY, gp.player);
                    gc.fillText(col + "," + row, screenX + 4, screenY + 12);
                }
            }
        }
        
        gc.setGlobalAlpha(1.0);
        gc.setFont(originalFont);
        gc.setLineWidth(originalLineWidth);
        gc.setStroke(originalStroke);
    }
    
    public void drawCollisionBoxes(GraphicsContext gc) {
        Floor currentFloor = gp.floorM.getCurrentFloor();
        Floor floorAbove = gp.floorM.getFloorAbove();
        
        if (currentFloor == null) return;
        
        for(int worldRow = 0; worldRow < GamePanel.MAX_WORLD_ROW; worldRow++) {
            for(int worldCol = 0; worldCol < GamePanel.MAX_WORLD_COL; worldCol++) {
                if (!MapUtils.isVisibleOnScreen(
                    worldCol * GamePanel.TILE_SIZE, 
                    worldRow * GamePanel.TILE_SIZE, 
                    gp)) continue;
                
                int worldX = worldCol * GamePanel.TILE_SIZE;
                int worldY = worldRow * GamePanel.TILE_SIZE;
                
                int screenX = gp.camera.getScreenX(worldX, gp.player);
                int screenY = gp.camera.getScreenY(worldY, gp.player);
                
                // Check current floor collisions
                drawFloorCollisions(gc, currentFloor, worldCol, worldRow, screenX, screenY, false);
                
                // Check floor above collisions
                if (floorAbove != null) {
                    drawFloorCollisions(gc, floorAbove, worldCol, worldRow, screenX, screenY, true);
                }
                
                drawEntityCollisions(gc, currentFloor, worldX, worldY, screenX, screenY);
            }
        }
    }
    
    private void drawEntityCollisions(GraphicsContext gc, Floor floor, int worldX, int worldY, 
            int screenX, int screenY) {
        // Draw NPC collision boxes
        for (int i = 0; i < gp.npc.length; i++) {
            if (gp.npc[i] != null) {
                // Get NPC position in world coordinates
                int npcWorldX = gp.npc[i].getWorldX();
                int npcWorldY = gp.npc[i].getWorldY();

                // Convert to screen coordinates
                int npcScreenX = gp.camera.getScreenX(npcWorldX, gp.player);
                int npcScreenY = gp.camera.getScreenY(npcWorldY, gp.player);

                // Draw NPC collision box
                if (gp.npc[i].collisionOn) {
                    gc.setFill(new Color(1, 0, 1, 0.3)); // Purple for NPC collision
                } else {
                    gc.setFill(new Color(0, 1, 0, 0.3)); // Green for NPC no collision
                }
                gc.fillRect(npcScreenX, npcScreenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

                // Draw interaction area using EventHandler's range check
                gc.setStroke(new Color(1, 1, 0, 0.5)); // Yellow for interaction area
                gc.setLineWidth(1);
                
                // Get NPC position in grid coordinates
                int npcCol = npcWorldX / GamePanel.TILE_SIZE;
                int npcRow = npcWorldY / GamePanel.TILE_SIZE;
                
                // Draw interaction tiles
                for (int row = npcRow - 1; row <= npcRow + 1; row++) {
                    for (int col = npcCol - 1; col <= npcCol + 1; col++) {
                        if (EventHandler.isWithinInteractionRange(col, row, npcCol, npcRow)) {
                            int interactX = gp.camera.getScreenX(col * GamePanel.TILE_SIZE, gp.player);
                            int interactY = gp.camera.getScreenY(row * GamePanel.TILE_SIZE, gp.player);
                            gc.strokeRect(interactX, interactY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                        }
                    }
                }

                // Draw NPC ID or info
                gc.setFill(Color.WHITE);
                gc.setFont(COORDINATE_FONT);
                gc.fillText("NPC " + i, npcScreenX + 4, npcScreenY + 12);
            }
        }
    }
    
    private void drawFloorCollisions(GraphicsContext gc, Floor floor, int worldCol, int worldRow, 
                                   int screenX, int screenY, boolean isFloorAbove) {
        int terrainTileNum = floor.getTerrainTile(worldCol, worldRow);
        int objectTileNum = floor.getObjectTile(worldCol, worldRow);
        
        boolean isTerrainCollidable = (terrainTileNum > 0 && gp.tileM.terrainTile[terrainTileNum].getCollision());
        boolean isObjectCollidable = (objectTileNum > 0 && gp.tileM.objectTile[objectTileNum].getCollision());
        
        // Different colors for different floors
        double alpha = isFloorAbove ? 0.2 : 0.3;
        
        // Draw terrain collisions
        if (isTerrainCollidable) {
            Color color = isFloorAbove ? Color.PURPLE : Color.RED;
            gc.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            gc.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }
        
        // Draw object collisions
        if (isObjectCollidable) {
            Color color = isFloorAbove ? Color.CYAN : Color.BLUE;
            gc.setFill(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
            gc.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }
        
        // Highlight stairs
        if (CollisionHandler.isStairsUp(objectTileNum) || CollisionHandler.isStairsDown(objectTileNum)) {
            gc.setFill(new Color(1, 1, 0, 0.3));  // Yellow for stairs
            gc.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
        }
    }
    
    public void drawCoordinates(GraphicsContext gc) {

        Font originalFont = gc.getFont();
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        
        LINE = 0;
        int playerWorldCol = gp.player.getWorldX() / GamePanel.TILE_SIZE;
        int playerWorldRow = gp.player.getWorldY() / GamePanel.TILE_SIZE;
        
        String worldBlock = String.format("World block: (%d, %d)", playerWorldCol, playerWorldRow);
        String worldCoord = String.format("World coordinate: (%d, %d)", 
                                        gp.player.getWorldX(), gp.player.getWorldY());
        String screenCoord = String.format("Screen coordinate: (%d, %d)", 
                                         gp.player.getScreenX(), gp.player.getScreenY());
        
        gc.fillText(worldBlock, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
        gc.fillText(worldCoord, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
        gc.fillText(screenCoord, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
        
        gc.setFont(originalFont);
    }
    
    public void drawEntityInfo(GraphicsContext gc) {
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        
        String collisionInfo = String.format("Collision: %s", gp.player.collisionOn);
        String directionInfo = String.format("Direction: %s", gp.player.getDirection());
		String cameraInfo = String.format("Camera: %s", gp.camera.getCameraInfo());
        
        gc.fillText(collisionInfo, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
        gc.fillText(directionInfo, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
        gc.fillText(cameraInfo, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE++));
    }
    
    public void drawTileInfo(GraphicsContext gc) {
        Floor currentFloor = gp.floorM.getCurrentFloor();
        if (currentFloor == null) return;
        
        
        int playerWorldCol = gp.player.getWorldX() / GamePanel.TILE_SIZE;
        int playerWorldRow = gp.player.getWorldY() / GamePanel.TILE_SIZE;
        
        int baseTile = currentFloor.getBaseTile(playerWorldCol, playerWorldRow);
        int terrainTile = currentFloor.getTerrainTile(playerWorldCol, playerWorldRow);
        int objectTile = currentFloor.getObjectTile(playerWorldCol, playerWorldRow);
        
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        
        String tileInfo = String.format("Current tiles - Terrain: %d, Object: %d, Base: %d", 
                                      terrainTile, objectTile, baseTile);
        gc.fillText(tileInfo, MARGIN_LEFT, FIRST_LINE_Y + (LINE_HEIGHT * LINE));

        LINE = 0;
    }
    
    public void drawGameState(GraphicsContext gc) {
        Font originalFont = gc.getFont();
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        
        // Game loop info
        String stateInfo = String.format("Game State: %s", GamePanel.gameState);
        
        // Draw on the right side of the screen
        int rightMargin = GamePanel.SCREEN_WIDTH - 200;
        
        gc.fillText(stateInfo, rightMargin, FIRST_LINE_Y + (LINE_HEIGHT * LINE));
        
        gc.setFont(originalFont);
    }
    
    public void drawFPS(GraphicsContext gc) {
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        gc.fillText("FPS: " + GamePanel.currentFPS, GamePanel.SCREEN_WIDTH - 75, FIRST_LINE_Y);
    }
    
    public void drawFloorInfo(GraphicsContext gc) {
        Font originalFont = gc.getFont();
        gc.setFont(DEBUG_FONT);
        gc.setFill(Color.RED);
        
        Floor currentFloor = gp.floorM.getCurrentFloor();
        Floor floorAbove = gp.floorM.getFloorAbove();
        
        if (currentFloor != null) {
            String currentFloorInfo = String.format("Current Floor: %s (Level %d)", 
                currentFloor.getName(), 
                gp.floorM.getFloorNumber());
                
            // Draw current floor info
            gc.fillText(currentFloorInfo, 
                GamePanel.SCREEN_WIDTH - 320, 
                FIRST_LINE_Y + (LINE_HEIGHT * ++LINE));
                
            // Draw floor above info if it exists
            if (floorAbove != null) {
                String floorAboveInfo = String.format("Floor Above: %s", floorAbove.getName());
                gc.fillText(floorAboveInfo,
                    GamePanel.SCREEN_WIDTH - 320,
                    FIRST_LINE_Y + (LINE_HEIGHT * ++LINE));
            }
        }
        
        gc.setFont(originalFont);
    }
}