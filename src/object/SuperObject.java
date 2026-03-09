package object;

import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import game.GamePanel;
import game.utils.MapUtils;

// Object name is conflicting with java.lang.Object, therefore SuperObject is used
public abstract class SuperObject {

	protected GamePanel gp;
	protected Image[] images;
	public String name;
	protected boolean collision = false;
	protected int worldX, worldY;
	protected int tileWidth = 1;
	protected int tileHeight = 1;
	protected boolean collected = false;
	protected String[] dialogues = new String[20];
	protected int dialogueIndex = 0;
	protected boolean interactable;
	
	public SuperObject(GamePanel gp) {
		this.gp = gp;
	}
	
	public abstract void update();

	public void draw(GraphicsContext gc){
		if (images == null || images[0] == null || collected) return;  // Don't draw if collected
		
		// Calculate screen position using camera
		int screenX = gp.camera.getScreenX(worldX, gp.player);
		int screenY = gp.camera.getScreenY(worldY, gp.player);
		
		// Only draw if object is visible on screen
		if (MapUtils.isVisibleOnScreen(worldX, worldY, gp)) {
			// Draw the object with proper size (based on tile dimensions)
			gc.drawImage(images[0], 
				screenX, 
				screenY,
				GamePanel.TILE_SIZE * tileWidth,
				GamePanel.TILE_SIZE * tileHeight
			);
			
			// Debug drawing
			if (gp.keyH.isDebugMode) {
				// Draw collision box
				if (collision) {
					gc.setStroke(Color.RED);
					gc.setLineWidth(1);
					gc.strokeRect(screenX, screenY, 
						GamePanel.TILE_SIZE * tileWidth,
						GamePanel.TILE_SIZE * tileHeight);
				}
				
				// Draw interaction indicator
				if (interactable && !collected) {
					gc.setStroke(Color.YELLOW);
					gc.setLineWidth(1);
					gc.strokeRect(screenX - 2, screenY - 2, 
							GamePanel.TILE_SIZE * tileWidth + 4,
							GamePanel.TILE_SIZE * tileHeight + 4);
				}
			}
		}
	}
	
	public int getWorldX() { return this.worldX; }
	public void setWorldX(int x) { this.worldX = x; }
	
	public int getWorldY() { return this.worldY; }
	public void setWorldY(int y) { this.worldY = y; }

	public boolean isCollected() { return collected; }
	public void setCollected(boolean collected) { this.collected = collected; }
	
	public String getCurrentDialogue() {
		if (dialogueIndex >= dialogues.length || dialogues[dialogueIndex] == null) {
			return "...";
		}
		return dialogues[dialogueIndex];
	}
	
	public boolean hasMoreDialogue() {
		return dialogueIndex + 1 < dialogues.length && dialogues[dialogueIndex + 1] != null;
	}
	
	public void nextDialogue() {
		if (hasMoreDialogue()) {
			dialogueIndex++;
		}
	}
	
	public void resetDialogue() {
		dialogueIndex = 0;
	}

	public boolean getCollision() { 
		return this.collision; 
	}

	public boolean checkCollision(int nextX, int nextY) {
		if (!collision || collected) return false;
		
		int objCol = worldX / GamePanel.TILE_SIZE;
		int objRow = worldY / GamePanel.TILE_SIZE;
		int checkCol = nextX / GamePanel.TILE_SIZE;
		int checkRow = nextY / GamePanel.TILE_SIZE;
		
		return objCol == checkCol && objRow == checkRow;
	}
	
    public abstract void interact();

    public boolean isInteractable() { return interactable && !collected; }

	public boolean hasCollision() { return collision; }

	public String getName() { return name; }

	public String[] getDialogues() { return dialogues; }
}
