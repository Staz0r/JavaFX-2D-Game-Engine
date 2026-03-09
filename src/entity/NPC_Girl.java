package entity;

import game.GamePanel;

public class NPC_Girl extends NPC {

	// NPC Settings
	private static final int BASE_SPEED = 1; // Slower movement speed
	
	public NPC_Girl(GamePanel gp) {

		super(gp);
		
		setName("Village Girl");
		direction = "down";
		setSpeed(BASE_SPEED);
		setBlockSize(GamePanel.TILE_SIZE);
		getImage("npc/zzk");
		
		// Set NPC's dialogues
		setDialogues(new String[]{
			"Hello! I'm the village girl.",
			"Nice weather today, isn't it?",
			"Take care!"
		});
	}

	@Override
	public void update() {
		// Call parent NPC update to handle random movement
		super.update();
	}
}