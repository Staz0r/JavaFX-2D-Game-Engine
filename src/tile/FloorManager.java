package tile;

import game.GamePanel;
import javafx.scene.canvas.GraphicsContext;

public class FloorManager {
	
	private final int MAX_FLOOR = 10;
	
	GamePanel gp;
	private Floor[] floors;
	private int currentFloorNum;

	public FloorManager(GamePanel gp) {
		
		this.gp = gp;
		this.floors = new Floor[MAX_FLOOR];
		this.currentFloorNum = 0;
		initializeFloors();
    }
	
	private void initializeFloors() {

		try {
			// Floor 1
			loadFloor(0, "Debug Island 1F",
				"/maps/world/floor1/Base.txt",
				"/maps/world/floor1/Terrain.txt",
				"/maps/world/floor1/Object.txt"
			);
			
			// Floor 2
			loadFloor(1, "Debug Island 2F",
				"/maps/world/floor2/Base.txt",
				"/maps/world/floor2/Terrain.txt",
				"/maps/world/floor2/Object.txt"
			);

			// Verify floors loaded correctly
			if (floors[0] == null || floors[1] == null) {
				gp.debugUtils.logError("Failed to initialize one or more floors", null);
			} else {
				gp.debugUtils.logFloorInfo("Successfully initialized " + 
					floors[0].getName() + " and " + floors[1].getName());
			}

		} catch (Exception e) {
			gp.debugUtils.logError("Error initializing floors", e);
		}
	}
	
    private void loadFloor(int index, String name, String basePath,
    					String terrainPath, String objectPath) {

		if (index >= 0 && index < MAX_FLOOR) {
			floors[index] = new Floor(gp, name);
			floors[index].loadFloorData(basePath, terrainPath, objectPath);
		}
	}
    
	public void changeFloor(int newFloor) {
		if (isValidFloor(newFloor)) {
			currentFloorNum = newFloor;
			gp.debugUtils.logFloorInfo("Changed to floor: " + currentFloorNum + 
									  " (" + floors[currentFloorNum].getName() + ")");
		}
	}
	
	public void draw(GraphicsContext gc) {
	    Floor current = floors[currentFloorNum];
	    if (current != null) {
	        // Draw the floor below if it exists (for overlay effect)
	        if (currentFloorNum > 0 && floors[currentFloorNum - 1] != null) {
	            floors[currentFloorNum - 1].drawBase(gc);
	            floors[currentFloorNum - 1].drawTerrain(gc);
	            floors[currentFloorNum - 1].drawObjects(gc);
	        }
	        
	        // Draw current floor
	        current.drawBase(gc);
	        current.drawTerrain(gc);
	        current.drawObjects(gc);
	        
	        // Draw the floor above if it exists (with transparency)
	        if (floors[currentFloorNum + 1] != null) {
	            gc.setGlobalAlpha(0.9);
	            floors[currentFloorNum + 1].drawBase(gc);
	            floors[currentFloorNum + 1].drawTerrain(gc);
	            floors[currentFloorNum + 1].drawObjects(gc);
	            gc.setGlobalAlpha(1.0);
	        }
	        
	        if (gp.keyH.isDebugMode) {
	            gp.debugUtils.draw(gc);
	        }
	    } else {
	        gp.debugUtils.logError("Current floor is null!", null);
	    }
	}
	
	
    public void handleFloorTransition(int col, int row) {
    	Floor currentFloor = getCurrentFloor();
    	if (currentFloor == null) return;
    	
        if (col < 0 || col >= GamePanel.MAX_WORLD_COL || 
                row < 0 || row >= GamePanel.MAX_WORLD_ROW) {
                return;
        }
    	
        // Check for stairs and handle transition
        if (currentFloor.hasStairsUp(col, row)) {
            if (isValidFloor(currentFloorNum + 1)) {
            	gp.player.collisionOn = false;
                changeFloor(currentFloorNum + 1);
                // Move player slightly away from stairs
                gp.player.setWorldY(gp.player.getWorldY() - GamePanel.TILE_SIZE);
                // Add delay before re-enabling collision
                scheduleCollisionReset();
            }
        } else if (currentFloor.hasStairsDown(col, row)) {
            if (isValidFloor(currentFloorNum - 1)) {
            	gp.player.collisionOn = false;
                changeFloor(currentFloorNum - 1);
                // Move player slightly away from stairs
                gp.player.setWorldY(gp.player.getWorldY() + GamePanel.TILE_SIZE);
                scheduleCollisionReset();
            }
    	}
    }
	
	public Floor getCurrentFloor() {

		return floors[currentFloorNum];
	}


    public Floor getFloorAbove() {

        if (currentFloorNum + 1 < MAX_FLOOR) {
            return floors[currentFloorNum + 1];
        }
        return null;
    }
    
	public Floor getFloorBelow() {

        if (currentFloorNum - 1 >= 0) {
            return floors[currentFloorNum - 1];
        }
        return null;
	}

    // Optional helper method to check if there's a floor above
    public boolean hasFloorAbove() {

        return currentFloorNum + 1 < MAX_FLOOR && floors[currentFloorNum + 1] != null;
    }
    
	public boolean hasFloorBelow() {

		return currentFloorNum - 1 >= 0 && floors[currentFloorNum - 1] != null;
	}

	private boolean isValidFloor(int floorNum) {

		return floorNum >= 0 && floorNum < MAX_FLOOR && floors[floorNum] != null;
	}
	
	public int getFloorNumber() {

		return currentFloorNum;
	}

    // Add new method to handle delayed collision reset
    private void scheduleCollisionReset() {
        // Set a flag to track transition frames
        gp.collisionH.setInStairTransition(true);
    }
}