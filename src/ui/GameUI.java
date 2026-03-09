package ui;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import game.utils.FontLoader;
import entity.NPC;
import game.GamePanel;
import object.SuperObject;

public class GameUI {
	
	private final GamePanel gp;
	
	private final Font ssmallFont;
	private final Font smallFont;
    private final Font normalFont;
    private final Font titleFont;
    private int menuIndex;
    private final String[] titleOptions = {"NEW GAME", "SETTINGS", "EXIT"};
    private final String[] settingsOptions = {"BGM VOLUME", "SFX VOLUME", "BACK"};

    // Volume settings
    private int bgmVolume;
    private int sfxVolume;

    private Font dialogueFont = Font.font("Arial", FontWeight.NORMAL, 20);
    
    // Add these variables to fix "variable not initialized" errors
    private double textWidth;
    private double textX;
    private double textY;
    
	// Dialogue UI Settings
	private final int dialogueX = 50;
	private final int dialogueY = GamePanel.SCREEN_HEIGHT - 200;
	private final int dialogueWidth = GamePanel.SCREEN_WIDTH - 100;
	private final int dialogueHeight = 150;
    
    public GameUI(GamePanel gp) {
    	
    	this.gp = gp;

        // Make fonts final and handle potential null values

        Font tempDialogue = FontLoader.getPokemonClassic(20);
        Font tempSSmall = FontLoader.getPokemonClassic(FontLoader.SSMALL_TEXT_SIZE);
        Font tempSmall= FontLoader.getPokemonClassic(FontLoader.SMALL_TEXT_SIZE);
        Font tempNormal = FontLoader.getPokemonClassic(FontLoader.MEDIUM_TEXT_SIZE);
        Font tempTitle = FontLoader.getPokemonClassic(FontLoader.LARGE_TEXT_SIZE);
        
        // If font is not found, use the default Arial
        this.ssmallFont = tempSSmall != null ? tempSSmall : Font.font("Arial", FontLoader.SSMALL_TEXT_SIZE);
        this.smallFont = tempSmall != null ? tempSmall : Font.font("Arial", FontLoader.SMALL_TEXT_SIZE);
        this.normalFont = tempNormal != null ? tempNormal : Font.font("Arial", FontLoader.MEDIUM_TEXT_SIZE);
        this.titleFont = tempTitle != null ? tempTitle : Font.font("Arial", FontLoader.LARGE_TEXT_SIZE);
        this.menuIndex = 0; // Initialize menuIndex
        
        // Fix volume initialization - don't multiply by 10 again
        this.bgmVolume = gp.sound.getBGMVolume();
        this.sfxVolume = (int)gp.sound.getSEVolume();

        this.dialogueFont = tempDialogue != null ? tempDialogue : dialogueFont;
       
    }
    
    public void drawTitleScreen(GraphicsContext gc) {
        // Store original settings
        Font originalFont = gc.getFont();
        
        // Background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
        
        // Title
        gc.setFill(Color.WHITE);
        gc.setFont(titleFont);
        String title = "NOT POKEMON GAME";
        double titleWidth = titleFont.getSize() * title.length();
        gc.fillText(title, 
            (GamePanel.SCREEN_WIDTH - titleWidth) / 2,
            GamePanel.SCREEN_HEIGHT / 3);
        
        // Menu Options
        gc.setFont(normalFont);
        for (int i = 0; i < titleOptions.length; i++) {
            if (menuIndex == i) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.WHITE);
            }
            
            double optionWidth = normalFont.getSize() * titleOptions[i].length();
            gc.fillText(titleOptions[i],
                (GamePanel.SCREEN_WIDTH - optionWidth) / 2,
                GamePanel.SCREEN_HEIGHT / 2 + (i * 50));
        }
        
        // Instructions
        gc.setFont(smallFont);
        gc.setFill(Color.GRAY);
        String instructions = "Use UP/DOWN to select, ENTER to confirm";
        double instrWidth = normalFont.getSize() * instructions.length() * 0.6;
        gc.fillText(instructions,
            (GamePanel.SCREEN_WIDTH - instrWidth) / 2,
            GamePanel.SCREEN_HEIGHT - 50);
            
        gc.setFont(originalFont);
    }
    
    public void drawSettingsScreen(GraphicsContext gc) {
        Font originalFont = gc.getFont();
        
        // Background with semi-transparency
        gc.setFill(new Color(0, 0, 0, 0.9));
        gc.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
        
        // Settings Title
        gc.setFill(Color.WHITE);
        gc.setFont(titleFont);
        String title = "SETTINGS";
        double titleWidth = titleFont.getSize() * title.length();
        gc.fillText(title,
            (GamePanel.SCREEN_WIDTH - titleWidth) / 2,
            GamePanel.SCREEN_HEIGHT / 3);
            
        gc.setFont(normalFont);
        int spacing = 80; // Increased spacing to accommodate volume bars
        
        for (int i = 0; i < settingsOptions.length; i++) {
            if (menuIndex == i) {
                gc.setFill(Color.YELLOW);
            } else {
                gc.setFill(Color.WHITE);
            }
            
            double optionWidth = normalFont.getSize() * settingsOptions[i].length();
            double textX = (GamePanel.SCREEN_WIDTH - optionWidth) / 2;
            double textY = GamePanel.SCREEN_HEIGHT / 2 + (i * spacing);
            gc.fillText(settingsOptions[i], textX, textY);
            
            // Draw volume bars for BGM and SFX under the text
            if (i < 2) { // Only for BGM and SFX options
                drawVolumeBar(gc, 
                    (GamePanel.SCREEN_WIDTH - 200) / 2, // Center the volume bar
                    textY + 15, // Position bar below text
                    i == 0 ? bgmVolume : sfxVolume);
                    
                // Display volume number
                String volumeText = String.format("%d/10", i == 0 ? bgmVolume : sfxVolume);
                double volumeWidth = normalFont.getSize() * volumeText.length() * 0.6;
                gc.fillText(volumeText, 
                    (GamePanel.SCREEN_WIDTH + 200) / 2 + 10, // Position after volume bar
                    textY + 30); // Align with volume bar
            }
        }
        
        gc.setFont(originalFont);
    }
    
    private void drawVolumeBar(GraphicsContext gc, double x, double y, int volume) {
        final int BAR_WIDTH = 200;
        final int BAR_HEIGHT = 20;
        final int SEGMENTS = 10;
        final int SEGMENT_WIDTH = BAR_WIDTH / SEGMENTS;
        
        // Draw background bar
        gc.setFill(Color.GRAY);
        gc.fillRect(x, y, BAR_WIDTH, BAR_HEIGHT);
        
        // Draw filled segments
        gc.setFill(Color.WHITE);
        for (int i = 0; i < volume; i++) {
            gc.fillRect(x + (i * SEGMENT_WIDTH), y, SEGMENT_WIDTH - 2, BAR_HEIGHT);
        }
    }

    // Add these methods to handle volume changes
    public void adjustVolume(int change) {
        if (menuIndex == 0) { // BGM Volume
            bgmVolume = Math.max(0, Math.min(10, bgmVolume + change));
            gp.sound.setBGMVolume(bgmVolume / 10.0);
        } else if (menuIndex == 1) { // SFX Volume
            sfxVolume = Math.max(0, Math.min(10, sfxVolume + change));
            gp.sound.setSEVolume(sfxVolume / 10.0);
        }
    }
    
    public void drawIngameMenu(GraphicsContext gc) {
        // Store original settings
        Font originalFont = gc.getFont();

        // Semi-transparent dark overlay
        gc.setFill(new Color(0, 0, 0, 0.5));
        gc.fillRect(0, 0, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);

        // Draw "MENU" text
        gc.setFill(Color.WHITE);
        gc.setFont(titleFont);
        String text = "MENU";
        textWidth = titleFont.getSize() * text.length();
        textX = (GamePanel.SCREEN_WIDTH - textWidth) / 2;
        textY = GamePanel.SCREEN_HEIGHT / 2 - 50;
        gc.fillText(text, textX, textY);

        // Draw "RESUME" button with highlight if selected
        gc.setFont(normalFont);
        text = "RESUME";
        double startTextWidth = normalFont.getSize() * text.length();
        if (menuIndex == 0) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.WHITE);
        }
        gc.fillText(text,
                (GamePanel.SCREEN_WIDTH - startTextWidth) / 2,
                GamePanel.SCREEN_HEIGHT / 2);

        // Draw "RESUME" button with highlight if selected
        gc.setFont(normalFont);
        text = "SETTINGS";
        double settingsTextWidth = normalFont.getSize() * text.length();
        if (menuIndex == 1) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.WHITE);
        }
        gc.fillText(text,
                (GamePanel.SCREEN_WIDTH - settingsTextWidth) / 2,
                GamePanel.SCREEN_HEIGHT / 2 + 50);
        
        // Draw "EXIT" button with highlight if selected
        text = "RETURN TO TITLE";
        double exitTextWidth = normalFont.getSize() * text.length();
        if (menuIndex == 2) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.WHITE);
        }
        gc.fillText(text,
                (GamePanel.SCREEN_WIDTH - exitTextWidth) / 2,
                GamePanel.SCREEN_HEIGHT / 2 + 100);

        // Restore original font
        gc.setFont(originalFont);
    }
    
    public void drawInteractionPrompt(GraphicsContext gc) {
    	
		// Save original settings
		Font originalFont = gc.getFont();
		Paint originalStroke = gc.getStroke();
		
		// Set up fonts
		Font keyFont = Font.font("Arial", FontWeight.BOLD, 18);
		Font textFont = Font.font("Arial", FontWeight.NORMAL, 16);
		
		gc.setFont(ssmallFont);
		
		// Calculate positions
		String preText = "Press ";
		String keyText = "F";
		String postText = " to interact";
		
		double keyWidth = 24;  // Width of the key background
		double keyHeight = 24; // Height of the key background
		double padding = 4;    // Padding inside key background
		
		// Calculate total width and center position
		double totalWidth = gc.getFont().getSize() * (preText.length() + postText.length()) * 0.6 + keyWidth;
		int centerX = (GamePanel.SCREEN_WIDTH - (int)totalWidth) / 2;
		int centerY = GamePanel.SCREEN_HEIGHT - 100;
		
		// Draw text with stroke
		gc.setLineWidth(3);  // Stroke width
		gc.setStroke(Color.BLACK);

		// Draw "Press"
		gc.setFill(Color.WHITE);
		gc.fillText(preText, centerX - 20, centerY);
		
		// Calculate key position
		double keyX = centerX + gc.getFont().getSize() * preText.length() * 0.6;
		double keyY = centerY - keyHeight + padding;
		
		// Draw key background
		gc.setFill(new Color(0.2, 0.2, 0.2, 0.9));  // Dark gray background
		gc.fillRoundRect(keyX, keyY + 4, keyWidth, keyHeight, 6, 6);
		
		// Draw key border
		gc.setStroke(Color.WHITE);
		gc.setLineWidth(1);
		gc.strokeRoundRect(keyX, keyY, keyWidth, keyHeight, 6, 6);
		
		// Reset stroke settings
		gc.setLineWidth(1);
		gc.setStroke(originalStroke);

		// Draw "F"
		gc.setFont(keyFont);
		gc.setFill(Color.WHITE);
		// Center the F in the box
		double fX = keyX + (keyWidth - keyFont.getSize() * 0.6) / 2;
		double fY = centerY;
		gc.fillText(keyText, fX, fY);

		// Draw text with stroke
		gc.setLineWidth(3);  // Stroke width
		gc.setStroke(Color.BLACK);

		// Draw "to interact"
		gc.setFont(ssmallFont);
		gc.fillText(postText, keyX + keyWidth + padding, centerY);
		
		// Restore original font
		
		gc.setLineWidth(1);
		gc.setStroke(originalStroke);
		gc.setFont(originalFont);
    }
    
    public void drawDialogueWindow(GraphicsContext gc) {
        NPC currentSpeaker = gp.dialogueH.getCurrentSpeaker();
        SuperObject currentObject = gp.dialogueH.getCurrentObject();

        if (currentSpeaker == null && currentObject == null) {
            return;
        }
        
        // Rounded border
        int cornerRadius = 10;
        
        // Save original graphics settings
        Font originalFont = gc.getFont();
        
        // Draw dialogue box background
        gc.setFill(new Color(0, 0, 0, 0.8));
        gc.fillRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, cornerRadius, cornerRadius);
        
        // Draw border
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRoundRect(dialogueX, dialogueY, dialogueWidth, dialogueHeight, cornerRadius, cornerRadius);
        
        // Draw name/title
        gc.setFont(dialogueFont);
        gc.setFill(Color.YELLOW);
        if (currentSpeaker != null) {
            gc.fillText(currentSpeaker.getName(), dialogueX + 20, dialogueY + 30);
        }

        // Draw current dialogue
        gc.setFill(Color.WHITE);
        gc.fillText(gp.dialogueH.getCurrentDialogue(), dialogueX + 20, dialogueY + 70);
        
        // Draw continue prompt
        gc.setFill(Color.LIGHTGRAY);
        String promptText = gp.dialogueH.hasMoreDialogue() ? 
            "Press F to continue..." : 
            "Press F to end dialogue...";
        gc.fillText(promptText, dialogueX + 20, dialogueY + dialogueHeight - 20);
        
        // Restore original graphics settings
        gc.setFont(originalFont);
    }
    
    public void drawControlHints(GraphicsContext gc) {
        // Save original settings
        Font originalFont = gc.getFont();
        Paint originalStroke = gc.getStroke();
        
        // Set up fonts
        Font keyFont = Font.font("Arial", FontWeight.BOLD, 16);
        
        // Control hints to display
        String[][] controls = {
            {"SHIFT", "Run"},
            {"F3", "Debug"},
            {"ESC", "Menu"}
        };
        
        // Position settings
        int startX = 20;
        int startY = GamePanel.SCREEN_HEIGHT - 100;
        int spacing = 30; // Vertical spacing between controls
        
        for (int i = 0; i < controls.length; i++) {
            String keyText = controls[i][0];
            String actionText = controls[i][1];
            
            // Key box dimensions
            double keyWidth = 50;  // Width of the key background
            double keyHeight = 24; // Height of the key background
            double padding = 4;    // Padding inside key background
            
            // Draw key background
            gc.setFill(new Color(0.2, 0.2, 0.2, 0.9));
            gc.fillRoundRect(startX, startY + (i * spacing), keyWidth, keyHeight, 6, 6);
            
            // Draw key border
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1);
            gc.strokeRoundRect(startX, startY + (i * spacing), keyWidth, keyHeight, 6, 6);
            
            // Draw key text
            gc.setFont(keyFont);
            gc.setFill(Color.WHITE);
            double textWidth = gc.getFont().getSize() * keyText.length() * 0.6;
            double textX = startX + (keyWidth - textWidth) / 2;
            gc.fillText(keyText, textX, startY + 17 + (i * spacing));
            
            // Draw action text
            gc.setFont(ssmallFont);
            gc.fillText(actionText, startX + keyWidth + padding + 5, startY + 17 + (i * spacing));
        }
        
        // Restore original settings
        gc.setLineWidth(1);
        gc.setStroke(originalStroke);
        gc.setFont(originalFont);
    }
    
    public void resetMenuIndex() {
    	menuIndex = 0;
    }

    public void updateMenuIndex(int change, int size) {
        menuIndex += change;
        
        // Clamp menuIndex to valid range
        if (menuIndex < 0) {
            menuIndex = 0;
        } else if (menuIndex > size - 1) {
            menuIndex = size - 1;
        }
    }

    public int getMenuIndex() {
        return menuIndex;
    }
}