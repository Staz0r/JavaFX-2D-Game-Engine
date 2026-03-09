package game;

import entity.Entity;
import object.SuperObject;

// Singleton class to handle the camera
public class Camera {
    private static Camera instance;
    private GamePanel gp;
    
    // Screen center coordinates
    private final int screenCenterX;
    private final int screenCenterY;
    
    // Player sprite offset constants
    private static final int PLAYER_OFFSET_X = 12;
    private static final int PLAYER_OFFSET_Y = 36;
    
    // Screen position constants
    private final int originalScreenX;
    private final int originalScreenY;
    
    private Camera() {
        // Calculate screen center
        screenCenterX = GamePanel.SCREEN_WIDTH / 2;
        screenCenterY = GamePanel.SCREEN_HEIGHT / 2;
        
        // Calculate original screen position
        originalScreenX = screenCenterX - GamePanel.TILE_SIZE / 2;
        originalScreenY = screenCenterY - GamePanel.TILE_SIZE / 2;
    }
    
	public static Camera init(GamePanel gp) {
		if (instance == null) {
			instance = new Camera();
			instance.gp = gp;
		}
		return instance;
	}
    
    // Initialize the camera instance for return
    public static Camera getInstance() {
        if (instance == null) {
            instance = new Camera();
        }
        return instance;
    }
    
    /**
     * Converts world coordinates to screen coordinates for any entity
     */
    
    // Fix offset calculation no hardcoding
    public int getScreenX(int worldX, Entity entity) {
        // Calculate base screen position relative to player
        int screenX = worldX - gp.player.getWorldX() + screenCenterX;
        
        // Add offset only for player entity
        if (entity == gp.player) {
            screenX += PLAYER_OFFSET_X;
        }
        
        return screenX;
    }
    
    public int getScreenY(int worldY, Entity entity) {
        // Calculate base screen position relative to player
        int screenY = worldY - gp.player.getWorldY() + screenCenterY;
        
        // Add offset only for player entity
        if (entity == gp.player) {
            screenY += PLAYER_OFFSET_Y;
        }
        
        return screenY;
    }
    
    // Getters for camera properties
    public int getScreenCenterX() { return screenCenterX; }
    public int getScreenCenterY() { return screenCenterY; }
    public int getOriginalScreenX() { return originalScreenX; }
    public int getOriginalScreenY() { return originalScreenY; }
    
    // Helper methods for debug purposes
    public String getCameraInfo() {
        return String.format("Camera Center: (%d, %d), Original Screen: (%d, %d)",
            screenCenterX, screenCenterY, originalScreenX, originalScreenY);
    }
}