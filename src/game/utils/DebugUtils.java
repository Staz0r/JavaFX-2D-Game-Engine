package game.utils;

import game.GamePanel;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import tile.Floor;

public class DebugUtils {
	
	private GamePanel gp;
	private DebugUI ui;
	
	// Debug flags
	private boolean showCollisionBoxes = true;
	private boolean showCoordinates = true;
	private boolean showGrid = true;
	private boolean showEntityInfo = true;
	private boolean showTileInfo = true;
	private boolean showGameState= true;
	private boolean showFPS = true;
	private boolean showFloorInfo = true;
	
	public DebugUtils(GamePanel gp) {
		this.gp = gp;
		this.ui = new DebugUI(gp);
	}
	
	public void toggleDebugMode() {
		gp.keyH.isDebugMode = !gp.keyH.isDebugMode;
		if (gp.keyH.isDebugMode) {
			System.out.println("Debug mode enabled");
		}
	}
	
	public void draw(GraphicsContext gc) {
		if (!gp.keyH.isDebugMode) return;
		
		if (showGrid) ui.drawGrid(gc);
		if (showCollisionBoxes) ui.drawCollisionBoxes(gc);
		if (showCoordinates) ui.drawCoordinates(gc);
		if (showEntityInfo) ui.drawEntityInfo(gc);
		if (showTileInfo) ui.drawTileInfo(gc);
		if (showGameState) ui.drawGameState(gc);
		if (showFloorInfo) ui.drawFloorInfo(gc);
		if (showFPS) ui.drawFPS(gc);
	}
	
	// Debug logging methods
	public void logCollision(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Collision] " + message);
		}
	}
	
	public void logCollisionAbove(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Collision Above] " + message);
		}
	}
	
	public void logStairsCollision(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Stairs Collision] " + message);
		}
	}
	
	public void logTileInfo(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Tile] " + message);
		}
	}
	
	public void logFloorInfo(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Floor] " + message);
		}
	}
	
	public void logError(String message, Exception e) {
		System.err.println("[Error] " + message);
		if (gp.keyH.isDebugMode) {
			e.printStackTrace();
		}
	}
	
	public void logCameraInfo(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Camera] " + message);
		}
	}
	
	public void logEvent(String message) {
		if (gp.keyH.isDebugMode) {
			System.out.println("[Event] " + message);
		}
	}
}
