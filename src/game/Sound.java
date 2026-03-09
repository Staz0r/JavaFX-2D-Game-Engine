package game;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Sound {

    // Sound paths
    private final String BGM_PATH = "/audios/Littleroot Town Theme.mp3";
    private final String SELECT_PATH = "/audios/Select.mp3";
	
	// Sound types
	public static final String BGM = "bgm";
	public static final String SELECT = "select";
	
	// MediaPlayers
	private MediaPlayer bgmPlayer;
	private Map<String, MediaPlayer> soundEffects;
	
    // Volume settings
    private double bgmVolume = 0.2;
    private double seVolume = 0.5;
    private boolean isMuted = false;
	
    public Sound() {
        soundEffects = new HashMap<>();
        initializeSounds();
    }
    
    private void initializeSounds() {
        try {
            // Initialize BGM
            Media bgm = new Media(getClass().getResource(BGM_PATH).toString());
            bgmPlayer = new MediaPlayer(bgm);
            bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            bgmPlayer.setVolume(bgmVolume);
            
            // Initialize sound effects
            loadSoundEffect(SELECT, SELECT_PATH);
            
        } catch (Exception e) {
            System.err.println("Error loading sounds: " + e.getMessage());
        }
    }
    
    private void loadSoundEffect(String type, String path) {
        try {
            Media sound = new Media(getClass().getResource(path).toString());
            MediaPlayer player = new MediaPlayer(sound);
            player.setVolume(seVolume);
            soundEffects.put(type, player);
        } catch (Exception e) {
            System.err.println("Error loading sound effect " + type + ": " + e.getMessage());
        }
    }
    
    public void playSoundEffect(String type) {
        if (!isMuted && soundEffects.containsKey(type)) {
            MediaPlayer player = soundEffects.get(type);
            player.stop();
            player.play();
        }
    }
    
    // BGM controls
    public void start() {
        if (!isMuted && bgmPlayer != null) {
            bgmPlayer.play();
        }
    }
    
    public void stop() {
        if (bgmPlayer != null) {
            bgmPlayer.stop();
        }
    }
    
    /* 
     * Volume controls 
     * */

    // BGM controls
    public void setBGMVolume(double volume) {
        this.bgmVolume = Math.max(0, Math.min(1, volume));
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(isMuted ? 0 : bgmVolume);
        }
    }
    
    // SFX controls
    public void setSEVolume(double volume) {
        this.seVolume = Math.max(0, Math.min(1, volume));
        for (MediaPlayer player : soundEffects.values()) {
            player.setVolume(isMuted ? 0 : seVolume);
        }
    }
    
    public void toggleMute() {
        isMuted = !isMuted;
        if (bgmPlayer != null) {
            bgmPlayer.setVolume(isMuted ? 0 : bgmVolume);
        }
        for (MediaPlayer player : soundEffects.values()) {
            player.setVolume(isMuted ? 0 : seVolume);
        }
    }
    
    // Getters for volume settings
    public int getBGMVolume() {
        return (int) (bgmVolume * 10);
    }
    
    public double getSEVolume() {
        return (int) (seVolume * 10);
    }
    
    public boolean isMuted() {
        return isMuted;
    }
    
    // Clean up resources
    public void dispose() {
        if (bgmPlayer != null) {
            bgmPlayer.dispose();
        }
        for (MediaPlayer player : soundEffects.values()) {
            player.dispose();
        }
        soundEffects.clear();
    }
}