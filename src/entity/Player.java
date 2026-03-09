package entity;

import javafx.scene.canvas.GraphicsContext;

import game.GamePanel;
import game.KeyHandler;

public class Player extends Entity{
	
	
	KeyHandler keyH;
	
	// Animation adjustment and smoothing
	private static final long DIRECTION_CHANGE_DELAY = 150; // milliseconds
	private long directionChangeTime = 0;
	private boolean justChangedDirection = false;
	
	private static final int SPEED = 2;
	private static final int BLOCKS_PER_MOVE = 1;

	public Player(GamePanel gp, KeyHandler keyH) {
		
		super(gp);
		this.keyH = keyH;
		direction = "up";
		setDefaultValues();
		getPlayerImage();
	}
	
	public void setDefaultValues() {

		// Set player's default position
		setWorldX(GamePanel.TILE_SIZE * 27);
		setWorldY(GamePanel.TILE_SIZE * 21);

		setTargetX(getWorldX());
		setTargetY(getWorldY());

		setSpeed(SPEED);
		setBlockSize(GamePanel.TILE_SIZE);
	}
	
	public void getPlayerImage() {
		
		getImage("player");
	}

	@Override
	public void update() {

		updateMovement();
	}
	
	private void updateMovement() {
		
	    final int currentBlockX = getWorldX() / getBlockSize();
	    final int currentBlockY = getWorldY() / getBlockSize();
	    
	    int nextBlockX = currentBlockX;
		int nextBlockY = currentBlockY;

	    // Check for edge
		final Boolean edgeXLeft = currentBlockX <= 0;
		final Boolean edgeXRight = currentBlockX >= GamePanel.MAX_WORLD_ROW - 1;
		final Boolean edgeYTop = currentBlockY <= 0;
		final Boolean edgeYBot = currentBlockY >= GamePanel.MAX_WORLD_COL - 1;
		
		// Establish boundary
	    final int maxBlockX = GamePanel.MAX_WORLD_ROW * getBlockSize();
	    final int maxBlockY = GamePanel.MAX_WORLD_COL * getBlockSize();
		
		boolean canMove = false;

		long currentTime = System.currentTimeMillis();
		boolean canChangeDirection = (currentTime - directionChangeTime) >= DIRECTION_CHANGE_DELAY;
		
		// If player is not moving, check for input
		// Default prioritize up movement
        if (!getIsMoving() && GamePanel.gameState == GamePanel.PLAY_STATE)	{
			// Axis of y is reversed on JavaFX canvas
			if (keyH.upPressed) {
				if (!direction.equals("up")) {
					setDirection("up");
					// Set the time of direction change
					directionChangeTime = currentTime;
					justChangedDirection = true;
				} else if (canChangeDirection || !justChangedDirection && !edgeYTop) {
					nextBlockY = currentBlockY - BLOCKS_PER_MOVE;
					canMove = true;
				}
			}
			else if (keyH.downPressed) {
				if (!direction.equals("down")) {
					setDirection("down");
					directionChangeTime = currentTime;
					justChangedDirection = true;
				} else if (canChangeDirection || !justChangedDirection && !edgeYBot) {
					nextBlockY = currentBlockY + BLOCKS_PER_MOVE;
					canMove = true;
				}
			}
			else if (keyH.leftPressed) {
				if (!direction.equals("left")) {
					setDirection("left");
					directionChangeTime = currentTime;
					justChangedDirection = true;
				} else if (canChangeDirection || !justChangedDirection && !edgeXLeft) {
					nextBlockX = currentBlockX - BLOCKS_PER_MOVE;
					canMove = true;
				}
			}
			else if (keyH.rightPressed) {
				if (!direction.equals("right")) {
					setDirection("right");
					directionChangeTime = currentTime;
					justChangedDirection = true;
				} else if (canChangeDirection || !justChangedDirection && !edgeXRight) {
					nextBlockX = currentBlockX + BLOCKS_PER_MOVE;
					canMove = true;
				}
			}

			if (!keyH.anyPressed) {
			    justChangedDirection = false;
			}
			
			collisionOn = false;
			gp.collisionH.checkTile(this);
			
			// If player is allowed to move, we reduce number of blocks to be within the boundary
			if (canMove) {
				// Restrict player movement within boundary
				nextBlockX = Math.max(0, Math.min(nextBlockX, maxBlockX)); 
				nextBlockY = Math.max(0, Math.min(nextBlockY, maxBlockY));

				// Set target position for player to new block in pixel instead of block
				setTargetX(nextBlockX * getBlockSize());
				setTargetY(nextBlockY * getBlockSize());
				
				if (keyH.isDebugMode) {
                    System.out.printf("Moving from block (%d,%d) to (%d,%d)%n", 
                            currentBlockX, currentBlockY, nextBlockX, nextBlockY);
				}
				
				if (!collisionOn) setIsMoving(true);
			}

			// If no key is pressed and player is not moving
			if (!keyH.anyPressed) {
				updateSprite(0);
			}
        } else {
             // Check if we've reached the current target
             boolean reachedTarget = (getWorldX() == getTargetX() && getWorldY() == getTargetY());
             
             if (keyH.upPressed && !edgeYTop) {
                 direction = "up";
                 if (reachedTarget) {
                     setTargetY((currentBlockY - BLOCKS_PER_MOVE) * GamePanel.TILE_SIZE);
                 }
             }
             else if (keyH.downPressed && !edgeYBot) {
                 direction = "down";
                 if (reachedTarget) {
                     setTargetY((currentBlockY + BLOCKS_PER_MOVE) * GamePanel.TILE_SIZE);
                 }
             }
             else if (keyH.leftPressed && !edgeXLeft) {
                 direction = "left";
                 if (reachedTarget) {
                     setTargetX((currentBlockX - BLOCKS_PER_MOVE) * GamePanel.TILE_SIZE);
                 }
             }
             else if (keyH.rightPressed && !edgeXRight) {
                 direction = "right";
                 if (reachedTarget) {
                     setTargetX((currentBlockX + BLOCKS_PER_MOVE) * GamePanel.TILE_SIZE);
                 }
             }
        }
			
		// If player is moving, set target position while target position is not reached
		if (getIsMoving()) {
            // Calculate distance to target
            int dx = getTargetX() - getWorldX();
            int dy = getTargetY() - getWorldY();
            int distance = Math.max(Math.abs(dx), Math.abs(dy));
            
            if (keyH.shiftPressed) {
            	setSpeed(SPEED * 2);
			} else {
				setSpeed(SPEED);
			}
            
            // Adjust speed based on remaining distance and current base speed
            int adjustedSpeed = Math.min(getSpeed(), 
                Math.max(getSpeed(), distance / MIN_FRAMES_PER_TILE));

			if (getWorldX() < getTargetX()) {
				setWorldX(Math.min(getWorldX() + adjustedSpeed, getTargetX()));
			}
			if (getWorldX() > getTargetX()) {
				setWorldX(Math.max(getWorldX() - adjustedSpeed, getTargetX()));
			}
			if (getWorldY() < getTargetY()) {
				setWorldY(Math.min(getWorldY() + adjustedSpeed, getTargetY()));
			}
			if (getWorldY() > getTargetY()) {
				setWorldY(Math.max(getWorldY() - adjustedSpeed, getTargetY()));
			}
			
			updateSprite(adjustedSpeed);
			
			// Check if player has reached target position
			if (getWorldX() == getTargetX() && getWorldY() == getTargetY()) {
				if (keyH.isDebugMode) {
					System.out.println("Player worldX: " + getWorldX() + ", worldY: " + getWorldY());
					System.out.println("Player screenX: " + camera.getScreenCenterX() + ", screenY: " + camera.getOriginalScreenY());
					System.out.println("Speed: " + getSpeed() + ", WorldX: " + getWorldX() + ", TargetX: " + getTargetX());
				}
				setIsMoving(false);
			}
			
		}
	}
	
	@Override
	public void draw(GraphicsContext gc) {
	
		drawShadow(gc, this);
		drawPlayer(gc);
	}
	
	
	private void drawPlayer(GraphicsContext gc) {

        gc.drawImage(getSprite(), gp.camera.getScreenCenterX(), gp.camera.getScreenCenterY(), GamePanel.TILE_SIZE * 3 / 2, GamePanel.TILE_SIZE * 3 / 2);
    }

	@Override
	public void snapToGrid() {
		// TODO Auto-generated method stub
		
	}
}
