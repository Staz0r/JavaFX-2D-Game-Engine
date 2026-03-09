package entity;

import javafx.scene.image.Image;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Color;
import javafx.scene.canvas.GraphicsContext;
import game.Camera;
import game.GamePanel;
import game.utils.MapUtils;

public abstract class Entity {
	protected GamePanel gp;
	private int worldX, worldY;
	private int targetX, targetY;
	private int speed;
	private int blockSize;
	private Boolean isMoving = false;
	private Image[] upSprites;
	private Image[] downSprites;
	private Image[] leftSprites;
	private Image[] rightSprites;
	
	protected static final int MIN_FRAMES_PER_TILE = 8;
	protected static final int FRAMES_PER_SPRITE = 15;
	private static final int FRAMES_PER_DIRECTION = 3;

	protected String direction;
	protected String name;
	
	protected int spriteCounter = 0;
	protected int spriteNum = 1;
	
	public boolean collisionOn = false;
	
	protected final Camera camera;

	public Entity(GamePanel gp) {
		this.gp = gp;
		this.camera = Camera.getInstance();
		this.worldX = 0;
		this.worldY = 0;
		this.direction = "down";
		this.speed = 1;
		this.blockSize = GamePanel.TILE_SIZE;
	}
	
	public abstract void update();

	public void draw(GraphicsContext gc) {

        if (MapUtils.isVisibleOnScreen(getWorldX(), getWorldY(), gp)){
			gc.drawImage(getSprite(), getScreenX(), getScreenY(), GamePanel.TILE_SIZE / 2 * 3, GamePanel.TILE_SIZE / 2 * 3);
			drawShadow(gc, this);
        }
	};
	
	public void updateSprite(int currentSpeed) {
		
		int pixelsPerFrame = currentSpeed;
		int framesPerTile = getBlockSize() / Math.max(pixelsPerFrame, 1);
		int animationSpeed = Math.max(framesPerTile / 3, MIN_FRAMES_PER_TILE);

		// Animation handler for idle sprite
		if (currentSpeed == 0) {
			spriteNum = 1;
			// If sprite is not default, set sprite to default
			if (spriteNum != 1) {
				spriteCounter++;
				if (spriteCounter > FRAMES_PER_SPRITE) {
					spriteCounter = 0;
					// Gradually decrease spriteNum to 1
					spriteNum = 1;
				}
			}
			return;
		}

		// Sprite animation handler
		spriteCounter++;

		// If spriteCounter exceeds animation speed, change sprite
		if (spriteCounter >= animationSpeed) {
			spriteCounter = 0;
			spriteNum++;
			spriteNum = (spriteNum > 4) ? spriteNum % 4 : spriteNum;
		}
	}
	
	protected void drawShadow(GraphicsContext gc, Entity entity) {
		int screenX = camera.getScreenX(getWorldX(), this);
		int screenY = camera.getScreenY(getWorldY(), this);
		
		if (entity == gp.player) {
			screenX = camera.getScreenCenterX();
			screenY = camera.getScreenCenterY();
		}
		
		double originalAlpha = gc.getGlobalAlpha();    
		Paint originalFill = gc.getFill();
		
		// Shadow properties
		double shadowWidth = GamePanel.TILE_SIZE * 0.8;
		double shadowHeight = GamePanel.TILE_SIZE * 0.6;
		
		// Calculate shadow position
		double shadowX = screenX + (GamePanel.TILE_SIZE * 3/2 - shadowWidth) / 2;
		double heightFromCenter = 1.15;
		double shadowY = screenY + GamePanel.TILE_SIZE * heightFromCenter;
		
	    // Pixelation settings
	    int pixelSize = 2; // Size of each "pixel" in the shadow
	    int pixelsWide = (int)(shadowWidth / pixelSize);
	    int pixelsHigh = (int)(shadowHeight / pixelSize);
		
		// Draw shadow
		gc.setGlobalAlpha(0.3);
		gc.setFill(Color.BLACK);
		
	    for (int y = 0; y < pixelsHigh; y++) {
	        for (int x = 1; x < pixelsWide; x++) {
	            // Calculate position within oval
	            double normalizedX = (x - pixelsWide/2.0) / (pixelsWide/2.0);
	            double normalizedY = (y - pixelsHigh/2.0) / (pixelsHigh/2.0);
	            
	            // Check if point is within oval equation: (x²/a²) + (y²/b²) ≤ 1
	            if ((normalizedX * normalizedX) + (normalizedY * normalizedY * 4) <= 1.0) {
	                gc.fillRect(
	                    shadowX + (x * pixelSize),
	                    shadowY + (y * pixelSize),
	                    pixelSize,
	                    pixelSize
	                );
	            }
	        }
	    }
		
	    // Vector version of shadow
	    // gc.fillOval(shadowX, shadowY, shadowWidth, shadowHeight);
		
		// Restore graphics state
		gc.setGlobalAlpha(originalAlpha);
		gc.setFill(originalFill);
	}
	    
	protected void getImage(String entityImageDirectory) {
		
		upSprites = new Image[FRAMES_PER_DIRECTION];
		downSprites = new Image[FRAMES_PER_DIRECTION];
		leftSprites = new Image[FRAMES_PER_DIRECTION];
		rightSprites = new Image[FRAMES_PER_DIRECTION];
		
		try {
            String[] directions = {"Up", "Down", "Left", "Right"};
            
            for (String dir : directions) {
                for (int frame = 0; frame < FRAMES_PER_DIRECTION; frame++) {
                    String path = String.format("/entity/%s/%s_%0" +
					"2d.png", entityImageDirectory, dir, frame + 1);

                    try {
                    	Image sprite = new Image(getClass().getResourceAsStream(path));
						// Store in appropriate array
						switch (dir.toLowerCase()) {
							case "up" -> upSprites[frame] = sprite;
							case "down" -> downSprites[frame] = sprite;
							case "left" -> leftSprites[frame] = sprite;
							case "right" -> rightSprites[frame] = sprite;
						}
					} catch (Exception e) {
						System.err.println("Error loading sprite: " + path);
					}
                    
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading player sprites: " + e.getMessage());
        }
	}
	
	protected Image getSprite() {
		Image[] currentDirectionSprites = switch (direction) {
			case "up" -> upSprites;
			case "down" -> downSprites;
			case "left" -> leftSprites;
			case "right" -> rightSprites;
			default -> downSprites;
		};

	    int index = switch(spriteNum) {
			case 1, 3 -> 0;  // First frame
			case 2 -> 1;     // Second frame
			case 4 -> 2;     // Third frame
			default -> 0;    // Default to first frame
	    };

		return currentDirectionSprites[index];
	}
	
	public int getWorldX() { return worldX; }
	public void setWorldX(int x) { this.worldX = x; }
	
	public int getWorldY() { return worldY; }
	public void setWorldY(int y) { this.worldY = y; }
	
	protected int getTargetX() { return targetX; }
	protected void setTargetX(int targetX) { this.targetX = targetX; }
	
	protected int getTargetY() { return targetY; }
	protected void setTargetY(int targetY) { this.targetY = targetY; }
	
	public int getSpeed() { return speed; }
	protected void setSpeed(int speed) { this.speed = speed; }
	
	public int getBlockSize() { return blockSize; }
	protected void setBlockSize(int blockSize) { this.blockSize = blockSize; }
	
	public Boolean getIsMoving() { return isMoving; }
	protected void setIsMoving(Boolean moving) { this.isMoving = moving; }
	
	public String getDirection() { return direction; }
	public void setDirection(String direction) { this.direction = direction; }
	
	public int getScreenX() { return camera.getScreenX(worldX, this); }
	public int getScreenY() { return camera.getScreenY(worldY, this); }
	
	public String getName() { return name; }

	public abstract void snapToGrid();
}