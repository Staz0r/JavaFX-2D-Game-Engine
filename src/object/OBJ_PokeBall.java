package object;

import game.GamePanel;
import javafx.scene.image.Image;

public class OBJ_PokeBall extends SuperObject {

	public OBJ_PokeBall(GamePanel gp) {
		
		super(gp);
		this.name = "PokeBall";
		this.tileWidth = 1;
		this.tileHeight = 1;
		this.collision = true;
		this.interactable = true;
		
		// Set up dialogues
		dialogues = new String[3];
		dialogues[0] = "You found a PokeBall!";
		dialogues[1] = "The PokeBall has been added...";
		dialogues[2] = "to your inventory.";
		
		// Initialize images array and load image
		images = new Image[1];
		try {
			images[0] = new Image(getClass().getResourceAsStream("/objects/pokeball.png"));
			if (images[0] == null) {
				System.err.println("Failed to load PokeBall image");
			}
		} catch (Exception e) {
			System.err.println("Error loading PokeBall image: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	public void update() {
		// Add any update logic here if needed
	}

	@Override
	public boolean hasMoreDialogue() {
		return dialogueIndex < dialogues.length - 1;
	}

	@Override
	public void interact() {
		// Add interaction logic here
		// For example: add to inventory, play sound, etc.
		if (!collected) {
			gp.dialogueH.speakObject(this);
		}
	}

}

