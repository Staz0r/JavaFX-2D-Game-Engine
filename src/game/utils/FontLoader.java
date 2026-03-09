package game.utils;

import javafx.scene.text.Font;
import java.io.InputStream;

public class FontLoader {
    // Store fonts as static fields for easy access
    public static Font POKEMON_CLASSIC;
    private static final String POKEMON_CLASSIC_PATH = "/fonts/Pokemon Classic.ttf";
    
    // Font sizes that we'll commonly use
    public static final double SSMALL_TEXT_SIZE = 12.0;
    public static final double SMALL_TEXT_SIZE = 16.0;
    public static final double MEDIUM_TEXT_SIZE = 24.0;
    public static final double LARGE_TEXT_SIZE = 32.0;
    
    static {
        // Load fonts when class is first used
        loadFonts();
    }
    
    private static void loadFonts() {
        try {
            // Load Pokemon Classic font
            InputStream is = FontLoader.class.getResourceAsStream(POKEMON_CLASSIC_PATH);
            POKEMON_CLASSIC = Font.loadFont(is, MEDIUM_TEXT_SIZE);
            
            if (POKEMON_CLASSIC == null) {
                System.err.println("Failed to load Pokemon Classic font");
                // Fall back to a system font if loading fails
                POKEMON_CLASSIC = Font.font("Arial");
            }
            
        } catch (Exception e) {
            System.err.println("Error loading fonts: " + e.getMessage());
            // Fall back to system font
            POKEMON_CLASSIC = Font.font("Arial");
        }
    }
    
    // Helper methods to get fonts at different sizes
    public static Font getPokemonClassic(double size) {
        try {
            InputStream is = FontLoader.class.getResourceAsStream(POKEMON_CLASSIC_PATH);
            return Font.loadFont(is, size);
        } catch (Exception e) {
            System.err.println("Error loading Pokemon Classic font at size " + size);
            return Font.font("Arial", size);
        }
    }
}