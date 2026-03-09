package tile;

import game.CollisionHandler;
import game.GamePanel;
import game.utils.MapUtils;
import javafx.scene.canvas.GraphicsContext;

public class Floor {
	
	private GamePanel gp;
	private String name;
	
	// Tile map for each layer
	private int[][] baseLayer;		// Base layer
	private int[][] terrainLayer;	// Terrain layer
	private int[][] objectLayer;	// Object layer
	
	public Floor(GamePanel gp, String name) {

		this.gp = gp;
		this.name = name;
		
		baseLayer = new int[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];
		terrainLayer = new int[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];
		objectLayer = new int[GamePanel.MAX_WORLD_COL][GamePanel.MAX_WORLD_ROW];
	}
	
	public void loadFloorData(String baseMapPath, String terrainMapPath, String objectMapPath) {

		loadLayer(baseLayer, baseMapPath);
		loadLayer(terrainLayer, terrainMapPath);
		loadLayer(objectLayer, objectMapPath);
	}
	
	private void loadLayer(int[][] layer, String filePath) {

		MapUtils.loadMapData(layer, filePath);
	}
	
		
	public void drawBase(GraphicsContext gc) {

		drawLayer(gc, baseLayer);
	}

	public void drawTerrain(GraphicsContext gc) {

		drawLayer(gc, terrainLayer);
	}
	
	public void drawObjects(GraphicsContext gc) {

		drawLayer(gc, objectLayer);
	}
	
	private void drawLayer(GraphicsContext gc, int[][] layer) {

	    Tile[] tileSet;
	    if (layer == baseLayer) {
	        tileSet = gp.tileM.baseTile;
	    } else if (layer == terrainLayer) {
	        tileSet = gp.tileM.terrainTile;
	    } else if (layer == objectLayer) {
	        tileSet = gp.tileM.objectTile;
	    } else {
	        gp.debugUtils.logError("Unknown layer type", null);
	        return;
	    }
	    
	    MapUtils.drawMapLayer(gc, gp, layer, tileSet, gp.keyH.isDebugMode);
	}
	
	public int getBaseTile(int col, int row) {

        return baseLayer[col][row];
	}
	
	public int getTerrainTile(int col, int row) {

		return terrainLayer[col][row];
	}
	
	public int getObjectTile(int col, int row) {
		if (col < 0 || col >= GamePanel.MAX_WORLD_COL || 
			row < 0 || row >= GamePanel.MAX_WORLD_ROW) {
			return 0;  // Return empty tile for invalid positions
		}
		return objectLayer[col][row];
	}
	
	public String getName() {

		return name;
	}
	
    // Special tile checking methods
	public boolean hasStairsUp(int col, int row) {
		if (col < 0 || col >= GamePanel.MAX_WORLD_COL || 
			row < 0 || row >= GamePanel.MAX_WORLD_ROW) {
			return false;
		}
		int tileNum = objectLayer[col][row];
		boolean hasStairs = CollisionHandler.isStairsUp(tileNum);
		if (hasStairs) {
			gp.debugUtils.logFloorInfo("Found stairs up at: " + col + "," + row);
		}
		return hasStairs;
	}

	public boolean hasStairsDown(int col, int row) {
		if (col < 0 || col >= GamePanel.MAX_WORLD_COL || 
			row < 0 || row >= GamePanel.MAX_WORLD_ROW) {
			return false;
		}
		int tileNum = objectLayer[col][row];
		boolean hasStairs = CollisionHandler.isStairsDown(tileNum);
		if (hasStairs) {
			gp.debugUtils.logFloorInfo("Found stairs down at: " + col + "," + row);
		}
		return hasStairs;
	}
}