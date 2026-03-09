package game;

import entity.Entity;
import tile.Floor;
import tile.TileManager;
import object.SuperObject;

public class CollisionHandler {
	
	GamePanel gp;
	
	private int entityCurrentBlockX;
	private int entityCurrentBlockY;
	public static boolean inStairTransition = false;
	
	public CollisionHandler(GamePanel gp) {

		this.gp = gp;
    }
	
	// Check if the entity is colliding with any tiles
	// For Player and NPC entities
	public void checkTile(Entity entity) {
	
		entityCurrentBlockX = entity.getWorldX();
		entityCurrentBlockY= entity.getWorldY();
		
		checkCollision(entity);
	}

	// For monster that might have different offset
	public void checkTile(Entity entity, int offsetX, int offsetY) {
	
		// TODO: Round up the entity's position to the nearest block, or round down to the nearest block for irregular entity size
		entityCurrentBlockX = entity.getWorldX() - offsetX;
		entityCurrentBlockY = entity.getWorldY() - offsetY;
		
		checkCollision(entity);
	}
	
	public void checkCollision(Entity entity) {
		
		//  Convert the entity's world coordinates to block coordinates
		entityCurrentBlockX /= GamePanel.TILE_SIZE;
		entityCurrentBlockY /= GamePanel.TILE_SIZE;
		
//		if (gp.keyH.isDebugMode) {
//			System.out.println("Entity current block: " + entityCurrentBlockX + " " + entityCurrentBlockY);
//		}
		
		int nextBlockX = entityCurrentBlockX;
		int nextBlockY = entityCurrentBlockY;
		
		switch (entity.getDirection()) {
		case "up":
			checkDirection(entity, entityCurrentBlockX, nextBlockY - 1);
			break;
		case "down":
			checkDirection(entity, entityCurrentBlockX, nextBlockY + 1);
			break;
		case "left":
			checkDirection(entity, nextBlockX - 1, entityCurrentBlockY);
			break;
		case "right":
			checkDirection(entity, nextBlockX + 1, entityCurrentBlockY);
			break;
		default:
			break;
		}
	}
	
	private void checkDirection(Entity entity, int nextBlockX, int nextBlockY) {
	    try {
	        if (!isValidPosition(nextBlockX, nextBlockY)) {
	            entity.collisionOn = true;
	            return;
	        }

	        Floor currentFloor = gp.floorM.getCurrentFloor();
	        if (currentFloor == null) return;

	        // Reset collision state
	        if (inStairTransition) {
	        	entity.collisionOn = false;
	        }
	        
			checkNPCCollision(entity);

			if (entity == gp.player) {
				int objectTileNum = currentFloor.getObjectTile(nextBlockX, nextBlockY);
				
				if (isStairsUp(objectTileNum) || isStairsDown(objectTileNum)) {
					entity.collisionOn = false;
					inStairTransition = true;
					gp.floorM.handleFloorTransition(nextBlockX, nextBlockY);
					return;
				}
			}
			
			if (isEmptyTile(currentFloor, nextBlockX, nextBlockY)) {
				entity.collisionOn = false;
				return;
			}

	        int terrainTileNum = currentFloor.getTerrainTile(nextBlockX, nextBlockY);
	        if (terrainTileNum > 0 && gp.tileM.terrainTile[terrainTileNum].getCollision()) {
	            entity.collisionOn = true;
	            return;
	        }

	        int objectTileNum = currentFloor.getObjectTile(nextBlockX, nextBlockY);
	        if (objectTileNum > 0 &&
	        	!isStairsUp(objectTileNum) &&
	        	!isStairsDown(objectTileNum) &&
		        gp.tileM.objectTile[objectTileNum].getCollision()) {
				entity.collisionOn = true;
				return;
	        }

	        // 4. Check collectible object collision
	        if (entity == gp.player && gp.objUtils != null) {
	            for (SuperObject obj : gp.objUtils.objects) {
	                if (obj != null && !obj.isCollected() && 
	                    obj.checkCollision(nextBlockX * GamePanel.TILE_SIZE, 
                                         nextBlockY * GamePanel.TILE_SIZE)) {
	                    entity.collisionOn = true;
	                    return;
	                }
	            }
	        }

	        // 5. Check NPC collision if entity is player
	        if (entity == gp.player) {
	            for (Entity npc : gp.npc) {
	                if (npc != null) {
	                    int npcCol = npc.getWorldX() / GamePanel.TILE_SIZE;
	                    int npcRow = npc.getWorldY() / GamePanel.TILE_SIZE;
	                    
	                    if (nextBlockX == npcCol && nextBlockY == npcRow) {
	                        entity.collisionOn = true;
	                        return;
	                    }
	                }
	            }
	        }
			
			if (gp.floorM.getFloorNumber() == 0) {
				Floor floorAbove = gp.floorM.getFloorAbove();
				if (floorAbove != null) {
					checkFloorCollision(floorAbove, nextBlockX, nextBlockY, entity, true);
				}
			}

	    } catch (Exception e) {
	        gp.debugUtils.logError("Error in checkDirection", e);
	        entity.collisionOn = true;
	    }
	}

	private boolean isValidPosition(int nextBlockX, int nextBlockY) {
	    if (nextBlockX < 0 || nextBlockY < 0 || 
	        nextBlockX >= GamePanel.MAX_WORLD_ROW || 
	        nextBlockY >= GamePanel.MAX_WORLD_COL) {
	        gp.debugUtils.logCollision("Out of bounds: " + nextBlockX + "," + nextBlockY);
	        return false;
	    }
	    return true;
	}
	
	public boolean checkPlayerCollision(Entity npc) {
	    // Get NPC's next position based on direction
	    int npcNextX = npc.getWorldX();
	    int npcNextY = npc.getWorldY();
	    
	    // Calculate next position based on direction
	    switch (npc.getDirection()) {
	        case "up" -> npcNextY -= GamePanel.TILE_SIZE;
	        case "down" -> npcNextY += GamePanel.TILE_SIZE;
	        case "left" -> npcNextX -= GamePanel.TILE_SIZE;
	        case "right" -> npcNextX += GamePanel.TILE_SIZE;
	    }
	    
	    // Convert to tile coordinates
	    int npcNextCol = npcNextX / GamePanel.TILE_SIZE;
	    int npcNextRow = npcNextY / GamePanel.TILE_SIZE;
	    int playerCol = gp.player.getWorldX() / GamePanel.TILE_SIZE;
	    int playerRow = gp.player.getWorldY() / GamePanel.TILE_SIZE;
	    
	    // Check both current and next position
	    boolean currentCollision = (npcNextCol == playerCol && npcNextRow == playerRow);
	    boolean nextCollision = false;
	    
	    if (gp.player.getIsMoving()) {
	        int playerNextCol = playerCol;
	        int playerNextRow = playerRow;
	        
	        switch (gp.player.getDirection()) {
	            case "up" -> playerNextRow--;
	            case "down" -> playerNextRow++;
	            case "left" -> playerNextCol--;
	            case "right" -> playerNextCol++;
	        }
	        
	        nextCollision = (npcNextCol == playerNextCol && npcNextRow == playerNextRow);
	    }
	    
	    return currentCollision || nextCollision;
	}
	
	private boolean checkFloorCollision(Floor floor, int nextBlockX, int nextBlockY, 
            Entity entity, boolean isFloorAbove) {
		String floorDesc = isFloorAbove ? "Floor above" : "Current floor";

		// Don't check collisions from floor below when on upper floor
		if (!isFloorAbove && gp.floorM.getFloorNumber() > 0) {
			return false;
		}

		// Check terrain collision first
		try {
			int terrainTileNum = floor.getTerrainTile(nextBlockX, nextBlockY);
			if (terrainTileNum > 0 && terrainTileNum < gp.tileM.terrainTile.length) {
				boolean hasCollision = gp.tileM.terrainTile[terrainTileNum].getCollision();
				gp.debugUtils.logCollision(floorDesc + " terrain tile " + terrainTileNum + 
						   " collision=" + hasCollision);
				if (hasCollision) {
					entity.collisionOn = true;
					return true;
				}
			}
		} catch (Exception e) {
			gp.debugUtils.logError("Error checking " + floorDesc.toLowerCase() + " terrain collision", e);
			}

		// Then check object collision
		try {
			int objectTileNum = floor.getObjectTile(nextBlockX, nextBlockY);
			if (objectTileNum > 0 && objectTileNum < gp.tileM.objectTile.length) {
				boolean hasCollision = gp.tileM.objectTile[objectTileNum].getCollision();
				gp.debugUtils.logCollision(floorDesc + " object tile " + objectTileNum + 
							   " collision=" + hasCollision);
				if (hasCollision && !isStairsUp(objectTileNum) && !isStairsDown(objectTileNum)) {
					entity.collisionOn = true;
					return true;
				}
			}
		} catch (Exception e) {
			gp.debugUtils.logError("Error checking " + floorDesc.toLowerCase() + " object collision", e);
		}
		return false;
	}
	
	private void checkNPCCollision(Entity entity) {
	    // Skip NPC collision check during stair transitions
	    if (inStairTransition || entity != gp.player) {
	        return;
	    }

	    int entityCol = entity.getWorldX() / GamePanel.TILE_SIZE;
	    int entityRow = entity.getWorldY() / GamePanel.TILE_SIZE;
	    
	    for (int i = 0; i < gp.npc.length; i++) {
	        if (gp.npc[i] != null) {
	            int npcCol = gp.npc[i].getWorldX() / GamePanel.TILE_SIZE;
	            int npcRow = gp.npc[i].getWorldY() / GamePanel.TILE_SIZE;
	            
	            // Only check collision, don't handle interaction here
	            switch (entity.getDirection()) {
	                case "up":
	                    if (entityCol == npcCol && entityRow - 1 == npcRow) {
	                        entity.collisionOn = true;
	                    }
	                    break;
	                case "down":
	                    if (entityCol == npcCol && entityRow + 1 == npcRow) {
	                        entity.collisionOn = true;
	                    }
	                    break;
	                case "left":
	                    if (entityCol - 1 == npcCol && entityRow == npcRow) {
	                        entity.collisionOn = true;
	                    }
	                    break;
	                case "right":
	                    if (entityCol + 1 == npcCol && entityRow == npcRow) {
	                        entity.collisionOn = true;
	                    }
	                    break;
	            }
	        }
	    }
	}

	public static boolean isStairsUp(int tileNum) {
		return tileNum == TileManager.STAIRS_UP1 || tileNum == TileManager.STAIRS_UP2
				|| tileNum == TileManager.STAIRS_UP3;
	}
	
	public static boolean isStairsDown(int tileNum) {
		return tileNum == TileManager.STAIRS_DOWN1 || tileNum == TileManager.STAIRS_DOWN2
				|| tileNum == TileManager.STAIRS_DOWN3;
	}

	private boolean isEmptyTile(Floor floor, int col, int row) {
	    // Get all layer tiles
	    int baseTileNum = floor.getBaseTile(col, row);
	    int terrainTileNum = floor.getTerrainTile(col, row);
	    int objectTileNum = floor.getObjectTile(col, row);
	    
	    // A tile is considered empty if:
	    // 1. No base tile exists (0 or invalid)
	    // 2. No terrain AND no object tiles exist
	    boolean isEmpty = baseTileNum <= 0 || 
	                     (terrainTileNum <= 0 && objectTileNum <= 0);
	    
	    if (isEmpty) {
	        gp.debugUtils.logCollision("Empty tile at: " + col + "," + row);
	    }
	    
	    return isEmpty;
	}

	public boolean checkNPCtoNPCCollision(Entity npc) {
	    // Get exact next position
	    int npcNextX = npc.getWorldX();
	    int npcNextY = npc.getWorldY();
	    
	    // Calculate next position based on direction and speed
	    switch (npc.getDirection()) {
	        case "up" -> npcNextY -= GamePanel.TILE_SIZE;
	        case "down" -> npcNextY += GamePanel.TILE_SIZE;
	        case "left" -> npcNextX -= GamePanel.TILE_SIZE;
	        case "right" -> npcNextX += GamePanel.TILE_SIZE;
	    }
	    
	    // Convert to tile coordinates (use integer division for exact tile positions)
	    int npcNextCol = npcNextX / GamePanel.TILE_SIZE;
	    int npcNextRow = npcNextY / GamePanel.TILE_SIZE;
	    
	    // Check collision with other NPCs
	    for (int i = 0; i < gp.npc.length; i++) {
	        if (gp.npc[i] != null && gp.npc[i] != npc) {  // Skip self
	            int otherCol = gp.npc[i].getWorldX() / GamePanel.TILE_SIZE;
	            int otherRow = gp.npc[i].getWorldY() / GamePanel.TILE_SIZE;
	            
	            // Debug collision checks
	            if (gp.keyH.isDebugMode) {
	                System.out.println("Checking NPC collision: " +
	                    "Next pos (" + npcNextCol + "," + npcNextRow + ") vs " +
	                    "Other NPC (" + otherCol + "," + otherRow + ")");
	            }
	            
	            // Check if next position would overlap
	            if (npcNextCol == otherCol && npcNextRow == otherRow) {
	                return true;
	            }
	        }
	    }
	    return false;
	}

	public void setInStairTransition(boolean state) {
	    inStairTransition = state;
	}
}