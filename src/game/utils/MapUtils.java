package game.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import game.GamePanel;
import tile.Floor;
import tile.Tile;

public class MapUtils {

	private static int debug = 0;
	
	// Load map data from map file
	public static void loadMapData(int[][] mapData, String filePath) {
		try {
			InputStream is = MapUtils.class.getResourceAsStream(filePath);
			if (is == null) {
				throw new IllegalArgumentException("Cannot find file: " + filePath);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int row = 0;
			while (row < GamePanel.MAX_WORLD_ROW) {
				String line = br.readLine();
				if (line == null)
					break;

				String[] numbers = line.split(" ");

				for (int col = 0; col < GamePanel.MAX_WORLD_COL; col++) {
					if (col >= numbers.length) {
						throw new RuntimeException("Invalid map file format: not enough columns in " + filePath);
					}
					mapData[col][row] = Integer.parseInt(numbers[col]);
				}
				row++;
			}
			br.close();

		} catch (Exception e) {
			System.err.println("Error loading map data: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	// Get visible area around player bounded by screen size
	public static void getVisibleArea(GamePanel gp, VisibleArea area) {
		
		int playerWorldCol = gp.player.getWorldX() / GamePanel.TILE_SIZE;
		int playerWorldRow = gp.player.getWorldY() / GamePanel.TILE_SIZE;

		// Range of visible area
		area.startCol = Math.max(0, playerWorldCol - GamePanel.MAX_SCREEN_COL / 2);
		area.endCol = Math.min(GamePanel.MAX_WORLD_COL, area.startCol + GamePanel.MAX_SCREEN_COL + 1);
		area.startRow = Math.max(0, playerWorldRow - (GamePanel.MAX_SCREEN_ROW / 2));
		area.endRow = Math.min(GamePanel.MAX_WORLD_ROW, area.startRow + GamePanel.MAX_SCREEN_ROW + 1);

        // Expand visible area by one block to prevent black border
		area.startCol = (area.startCol == 0) ? area.startCol : area.startCol - 1;
		area.endCol = (area.endCol == GamePanel.MAX_WORLD_COL) ? area.endCol : area.endCol + 1;
		area.startRow = (area.startRow == 0) ? area.startRow : area.startRow - 1;
		area.endRow = (area.endRow == GamePanel.MAX_WORLD_ROW) ? area.endRow : area.endRow + 1;
	}
	
	// Draw map layer
    public static void drawMapLayer(GraphicsContext gc, GamePanel gp, int[][] mapData, 
                                  Tile[] tiles, boolean debug) {
        VisibleArea area = new VisibleArea();
        getVisibleArea(gp, area);
        
        for (int worldRow = area.startRow; worldRow < area.endRow; worldRow++) {
            for (int worldCol = area.startCol; worldCol < area.endCol; worldCol++) {
                int tileNum = mapData[worldCol][worldRow];
                
                if (tileNum <= 0 || tiles[tileNum].image == null) continue;

                int worldX = worldCol * GamePanel.TILE_SIZE;
                int worldY = worldRow * GamePanel.TILE_SIZE;

				gc.drawImage(tiles[tileNum].image,
						gp.camera.getScreenX(worldX, gp.player),
						gp.camera.getScreenY(worldY, gp.player),
						GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
            }
        }
    }
    
    
    
    public static void drawDebugGrid(GraphicsContext gc, GamePanel gp) {
    	
    	// TODO: Move this to a separate class
    	drawCollisionGrid(gc, gp);
    	drawHelperUI(gc, gp);
	}
    
    private static void drawCollisionGrid(GraphicsContext gc, GamePanel gp) {

    	Floor currentFloor = gp.floorM.getCurrentFloor();
        
        for(int worldRow = 0; worldRow < GamePanel.MAX_WORLD_ROW; worldRow++) {
            for(int worldCol = 0; worldCol < GamePanel.MAX_WORLD_COL; worldCol++) {
                int worldX = worldCol * GamePanel.TILE_SIZE;
                int worldY = worldRow * GamePanel.TILE_SIZE;
                int screenX = gp.camera.getScreenX(worldX, gp.player);
                int screenY = gp.camera.getScreenY(worldY, gp.player);
                
                // Only draw if on screen
                if (isVisibleOnScreen(screenX, screenY, gp)) {
                    // Check collision for both terrain and object layers
                    int terrainTileNum = currentFloor.getTerrainTile(worldCol, worldRow);
                    int objectTileNum = currentFloor.getObjectTile(worldCol, worldRow);
                    
                    boolean isCollidable = (terrainTileNum > 0 && gp.tileM.terrainTile[terrainTileNum].getCollision()) ||
                                         (objectTileNum > 0 && gp.tileM.objectTile[objectTileNum].getCollision());
                    
                    // Fill collidable tiles with semi-transparent red
                    if (isCollidable) {
                        gc.setFill(new Color(1, 0, 0, 0.3)); // Red with 30% opacity
                        gc.fillRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                    }
                    
                    // Draw coordinates for debugging
                    gc.setFill(Color.WHITE);
                    gc.setFont(Font.font(10));
                    gc.fillText(worldCol + "," + worldRow, screenX + 4, screenY + 12);
                }
            }
        }
    }
    
	private static void drawHelperUI(GraphicsContext gc, GamePanel gp) {

		double originalLineWidth = gc.getLineWidth() * 2;
		Paint originalStroke = gc.getStroke();
		Font originalFont = gc.getFont();
		
		gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));

		int playerWorldCol = gp.player.getWorldX() / GamePanel.TILE_SIZE;
		int playerWorldRow = gp.player.getWorldY() / GamePanel.TILE_SIZE;

		gc.setStroke(Color.GREEN);
		gc.setLineWidth(1);
		gc.setGlobalAlpha(0.3);
		
		// Draw vertical lines
		for (int x = 0; x <= GamePanel.SCREEN_WIDTH; x += GamePanel.TILE_SIZE) {
			gc.strokeLine(x, 0, x, GamePanel.SCREEN_HEIGHT);
		}
		
		// Draw horizontal lines	
		for (int y = 0; y <= GamePanel.SCREEN_HEIGHT; y += GamePanel.TILE_SIZE) {
			gc.strokeLine(0, y, GamePanel.SCREEN_WIDTH, y);
		}
		
	    int screenX = gp.camera.getScreenX(gp.player.getWorldX(), gp.player);
	    int screenY = gp.camera.getScreenY(gp.player.getWorldY(),  gp.player);
	    
	    // Mark current position
	    gc.setStroke(Color.RED);
	    gc.strokeRect(screenX, screenY, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);

	    // Reset graphics properties
	    gc.setGlobalAlpha(1.0);
	    gc.setLineWidth(originalLineWidth);
	    gc.setStroke(originalStroke);
	    
	    // Draw coordinates
	    gc.setFill(Color.RED);

	    String worldCoorBlock = String.format("World block: (%d, %d)", playerWorldCol, playerWorldRow);
	    String worldCoorCart = String.format("World coordinate: (%d, %d)", gp.player.getWorldX(), gp.player.getWorldY());
	    String screenCoorCart = String.format("Screen coordinate: (%d, %d)", screenX, screenY);
	    String collisionInfo = String.format("Collision: " + gp.player.collisionOn);

	    gc.fillText(worldCoorBlock, 10, 20);
	    gc.fillText(worldCoorCart, 10, 40);
	    gc.fillText(screenCoorCart, 10, 60);
	    gc.fillText(collisionInfo, 10, 80);

	    // Reset font
	    gc.setFont(originalFont);
	}
	
	public static void drawFPS(GraphicsContext gc, int FPS) {
		String fpsText = "FPS: " + FPS;
		
		Font originalFont = gc.getFont();
		
		gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		
	    gc.setFill(Color.RED);
		
		gc.fillText(fpsText, GamePanel.SCREEN_WIDTH - 75, 20);
		
		gc.setFont(originalFont);
		
    }
	
	// Check if a tile is visible on screen
	public static boolean isVisibleOnScreen(int worldX, int worldY, GamePanel gp) {
		return worldX + GamePanel.TILE_SIZE > gp.player.getWorldX() - gp.player.getScreenX() &&
			   worldX - GamePanel.TILE_SIZE < gp.player.getWorldX() + gp.player.getScreenX() &&
			   worldY + GamePanel.TILE_SIZE > gp.player.getWorldY() - gp.player.getScreenY() &&
			   worldY - GamePanel.TILE_SIZE < gp.player.getWorldY() + gp.player.getScreenY();
	}
}

class VisibleArea {
		
	public int startCol, endCol, startRow, endRow;
}
