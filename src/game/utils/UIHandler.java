package game.utils;

import entity.Entity;

import game.EventHandler;
import game.GamePanel;
import game.Sound;
import ui.GameUI;

import javafx.scene.canvas.GraphicsContext;
import object.SuperObject;

public class UIHandler {
    private final GamePanel gp;
    private final GameUI ui;

    private boolean showInteractionPrompt = false;
    private Entity nearbyNPC = null;
    
    // Menu cooldown
    private long lastInputTime = 0;
    private long lastVolumeChangeTime = 0;
    private static final long INPUT_COOLDOWN = 150;
    private static final long VOLUME_CHANGE_COOLDOWN = 200;
    
    // Add a field to track where settings were accessed from
    private boolean settingsFromMenu = false;
    
    public UIHandler(GamePanel gp) {

        this.gp = gp;
        this.ui = gp.ui;
    }
    
    public void update() {
        long currentTime = System.currentTimeMillis();
        boolean canInput = (currentTime - lastInputTime) >= INPUT_COOLDOWN;

        if (canInput) {
            // Handle escape key for menu toggle
            if (gp.keyH.escapePressed) {
                handleEscapeKey();
                lastInputTime = currentTime;
                return;
            }

            switch (GamePanel.gameState) {
                case GamePanel.TITLE_STATE -> handleTitleInput();
                case GamePanel.MENU_STATE -> handleMenuInput();
                case GamePanel.SETTINGS_STATE -> handleSettingsInput();
                case GamePanel.PLAY_STATE -> checkNPCProximity();
            }
            
            if (gp.keyH.upPressed || gp.keyH.downPressed || gp.keyH.enterPressed) {
                lastInputTime = currentTime;
            }
        }
    }

    public void checkNPCProximity() {
        showInteractionPrompt = false;
        nearbyNPC = null;
        
        // First check NPCs
        for (Entity npc : gp.npc) {
            if (npc != null && EventHandler.isNPCInFacingDirection(gp.player, npc)) {
                showInteractionPrompt = true;
                nearbyNPC = npc;
                return;
            }
        }
        
        // Then check objects
        if (gp.objUtils != null) {
            for (SuperObject obj : gp.objUtils.objects) {
                if (obj != null && obj.isInteractable() && 
                    EventHandler.isObjectInFacingDirection(gp.player, obj)) {
                    showInteractionPrompt = true;
                    return;
                }
            }
        }
    }
    
    private void handleEscapeKey() {
        switch (GamePanel.gameState) {
            case GamePanel.PLAY_STATE -> {
                // Toggle to menu state
                GamePanel.gameState = GamePanel.MENU_STATE;
                gp.ui.resetMenuIndex();
                playSelectSound();
            }
            case GamePanel.MENU_STATE -> {
                // Return to play state
                GamePanel.gameState = GamePanel.PLAY_STATE;
                playSelectSound();
            }
            case GamePanel.SETTINGS_STATE -> {
                // Return to menu or title based on where we came from
                if (settingsFromMenu) {
                    GamePanel.gameState = GamePanel.MENU_STATE;
                } else {
                    GamePanel.gameState = GamePanel.TITLE_STATE;
                }
                gp.ui.resetMenuIndex();
                playSelectSound();
            }
        }
    }
    
    private void handleTitleInput() {
        if (gp.keyH.upPressed) {
            gp.ui.updateMenuIndex(-1, 3);
            playSelectSound();
        } else if (gp.keyH.downPressed) {
            gp.ui.updateMenuIndex(1, 3);
            playSelectSound();
        } else if (gp.keyH.enterPressed) {
            switch (gp.ui.getMenuIndex()) {
                case 0: // NEW GAME
                	gp.player.setDefaultValues();
                    gp.setupGame();
                    GamePanel.gameState = GamePanel.PLAY_STATE;
                    playSelectSound();
                    break;
                case 1: // SETTINGS
                    GamePanel.gameState = GamePanel.SETTINGS_STATE;
                    settingsFromMenu = false; // Set flag when entering from title
                    gp.ui.resetMenuIndex();
                    playSelectSound();
                    break;
                case 2: // EXIT
                    System.exit(0);
                    break;
            }
        }
    }
    
    private void handleSettingsInput() {
    	
    	long currentTime = System.currentTimeMillis();
    	boolean canChangeVolume = (currentTime - lastVolumeChangeTime) >= VOLUME_CHANGE_COOLDOWN;
    	
        if (gp.keyH.upPressed) {
            gp.ui.updateMenuIndex(-1, 3);
            playSelectSound();
        } else if (gp.keyH.downPressed) {
            gp.ui.updateMenuIndex(1, 3);
            playSelectSound();
        } else if (canChangeVolume && (gp.keyH.leftPressed || gp.keyH.rightPressed)) {
            if (gp.ui.getMenuIndex() < 2) {
                gp.ui.adjustVolume(gp.keyH.leftPressed ? - 1 : 1);
                playSelectSound();
                lastVolumeChangeTime = currentTime;
            }
        } else if (gp.keyH.enterPressed) {
            switch (gp.ui.getMenuIndex()) {
                case 0: // KEYBINDS
                    // TODO: Implement keybind configuration
                    playSelectSound();
                    break;
                case 1: // SOUND
                    // TODO: Implement sound settings
                    playSelectSound();
                    break;
                case 2: // BACK
                    GamePanel.gameState = GamePanel.TITLE_STATE;
                    gp.ui.resetMenuIndex();
                    playSelectSound();
                    break;
            }
        } else if (gp.keyH.escapePressed) {
            GamePanel.gameState = GamePanel.TITLE_STATE;
            gp.ui.resetMenuIndex();
            playSelectSound();
        }
    }
    
    private void handleMenuInput() {
        if (gp.keyH.upPressed) {
            gp.ui.updateMenuIndex(-1, 3);
            playSelectSound();
        } else if (gp.keyH.downPressed) {
            gp.ui.updateMenuIndex(1, 3);
            playSelectSound();
        } else if (gp.keyH.enterPressed) {
            switch (gp.ui.getMenuIndex()) {
                case 0: // RESUME
                    GamePanel.gameState = GamePanel.PLAY_STATE;
                    playSelectSound();
                    break;
                case 1: // SETTINGS
                    GamePanel.gameState = GamePanel.SETTINGS_STATE;
                    settingsFromMenu = true; // Set flag when entering from menu
                    gp.ui.resetMenuIndex();
                    playSelectSound();
                    break;
                case 2: // EXIT
                	GamePanel.gameState = GamePanel.TITLE_STATE;
                	playSelectSound();
                    break;
            }
        }
    }
    
    public void draw(GraphicsContext gc) {

    	   switch (GamePanel.gameState) {
           case GamePanel.TITLE_STATE -> {
               // Draw title screen (full screen)
               ui.drawTitleScreen(gc);
           }
           case GamePanel.SETTINGS_STATE -> {
               // Draw settings screen (full screen or overlay depending on previous state)
               ui.drawSettingsScreen(gc);
           }
           case GamePanel.MENU_STATE -> {
               // Draw menu overlay on top of game world
               ui.drawIngameMenu(gc);
           }
           case GamePanel.PLAY_STATE -> {
               // Draw any play state UI elements
               ui.drawControlHints(gc);
               if (showInteractionPrompt) {
                   ui.drawInteractionPrompt(gc);
               }
           }
           case GamePanel.DIALOGUE_STATE -> {
               // Draw dialogue window on top of game world
               ui.drawDialogueWindow(gc);
           }
        }
    }
    

    
    private void playSelectSound() {
    	gp.sound.playSoundEffect(Sound.SELECT);
    }
}