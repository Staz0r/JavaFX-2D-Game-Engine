package game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import entity.Player;
import entity.Entity;
import game.utils.DebugUtils;
import game.utils.NPCUtils;
import game.utils.ObjectUtils;
import game.utils.UIHandler;
import tile.FloorManager;
import tile.TileManager;
import ui.GameUI;
import object.SuperObject;

public class GamePanel {
	
	// SCREEN SETTINGS
    public static final int ORIGINAL_TILE_SIZE = 16;
    public static final int SCALE = 3;
    public static final int TILE_SIZE = ORIGINAL_TILE_SIZE * SCALE;
    public static final int MAX_SCREEN_COL = 16;
    public static final int MAX_SCREEN_ROW = 12;
    public static final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_COL;
    public static final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_ROW;
    
    // WORLD SETTINGS
    public static final int MAX_WORLD_COL = 50;
    public static final int MAX_WORLD_ROW = 50;
    public static final int WORLD_WIDTH = TILE_SIZE * MAX_WORLD_COL;
    public static final int WORLD_HEIGHT = TILE_SIZE * MAX_WORLD_ROW;
    
    // GAME STATES
    public static final int TITLE_STATE = 0;
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    public static final int MENU_STATE = 3;
    
		// SPECIFIC GAME STATES
		public static final int DIALOGUE_STATE = 4;
	
	public static final int SETTINGS_STATE = 5;

    public static int gameState = TITLE_STATE;
    
    // FPS SETTINGS
    private static final int FPS = 60;
    private static final double DRAW_INTERVAL = 1e9 / FPS;
    public static int currentFPS = 0;

    // Core components
    private final Canvas canvas;
    private final GraphicsContext gc;
    private AnimationTimer gameLoop;
    public final Camera camera;
    public final Sound sound;
    
    // UI components
    public final GameUI ui;
    public final UIHandler uiH;
    
    // Game loop variables
    private double delta = 0;
    private int frameCount = 0;
    private long lastTime;
    
    // Required utility classes
    public KeyHandler keyH;
    public DebugUtils debugUtils;
    public CollisionHandler collisionH;
    public EventHandler eventH;
    public DialogueHandler dialogueH;
    
    // Game state managers
    public TileManager tileM;
    public FloorManager floorM;
    
    // Entity related
    public Player player;
    public Entity npc[] = new Entity[10];
    
    // Optional utilities
    public ObjectUtils objUtils;
    public NPCUtils npcUtils;
    
    public GamePanel() {
        // 1. Initialize core components
        this.canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
        this.gc = canvas.getGraphicsContext2D();
        this.camera = Camera.init(this);
        this.sound = new Sound();
        this.ui = new GameUI(this);
        
        // 2. Initialize required utilities
        this.keyH = new KeyHandler();
        this.debugUtils = new DebugUtils(this);
        this.collisionH = new CollisionHandler(this);
        this.dialogueH = new DialogueHandler(this);
        this.eventH = new EventHandler(this);
        this.uiH = new UIHandler(this);
        
        // 3. Initialize game state managers
        this.tileM = new TileManager(this);
        this.floorM = new FloorManager(this);
        
        // 4. Initialize entities
        this.player = new Player(this, keyH);
        this.npcUtils = new NPCUtils(this);

        // Initialize ObjectUtils before starting game
        this.objUtils = new ObjectUtils(this);

        // Start game
        start();
    }	

	public void setupGame() {
		
		if (npcUtils != null) {
			npcUtils.initializeNPCs(gc);
		}
		
		sound.start();
	}
	
	public Canvas getCanvas() {

		return canvas;
	}
	
	public void start() {

		gameLoop = new AnimationTimer() {

			private long timer = 0;
			private int finalFrameCount = 0;

			@Override
			public void handle(long currentTime) {
				
				delta += (currentTime - lastTime) / DRAW_INTERVAL;
				timer += (currentTime - lastTime);
				lastTime = currentTime;
				
				// Accumulated delta ensure correct FPS counter
				while (delta >= 1) {
					update();
					delta--;
					frameCount++;
				}

				render(gc);

				if (timer >= 1e9) {
					finalFrameCount = frameCount;
					frameCount = 0;
					timer = 0;
				}

				GamePanel.currentFPS = finalFrameCount;
			}
		};
		
		gameLoop.start();
	}
	
	private void update() {

		if (gameState == PLAY_STATE) {
			// Update player first to handle movement and potential floor transitions
			player.update();
			
			// Update NPCs
			for(int i = 0; i < npc.length; i++) {
				if(npc[i] != null) {
					npc[i].update();
				}
			}
			eventH.checkEvent();
		} else if (gameState == DIALOGUE_STATE) {
			for(int i = 0; i < npc.length; i++) {
				if(npc[i] != null) {
					npc[i].update();
				}
			}
			dialogueH.handleDialogueInput();
		} else if (gameState == MENU_STATE) {

		}

		uiH.update();
		
		if (keyH.isDebugMode) {
			debugUtils.logTileInfo("Current Floor: " + floorM.getCurrentFloor().getName());
		}
	}
	
	private void render(GraphicsContext gc) {
		// Render to be written later
		gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

		gc.setFill(Color.BLACK);
		gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
		
		// Draw order
		floorM.draw(gc);
		if (objUtils != null) {
			for (SuperObject obj : objUtils.objects) {
				if (obj != null) {
					obj.draw(gc);
				}
			}
		}
		npcUtils.drawNPCs(gc);
		player.draw(gc);
		
		// Draw all UI elements through UIHandler
		uiH.draw(gc);
		
		if (keyH.isDebugMode) {
			debugUtils.draw(gc);
		}
	}
}
