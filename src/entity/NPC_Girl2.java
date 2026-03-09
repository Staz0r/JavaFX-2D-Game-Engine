package entity;

import game.GamePanel;

public class NPC_Girl2 extends NPC {

	// NPC Settings
	private static final int BASE_SPEED = 1; // Slower movement speed
	
	public NPC_Girl2(GamePanel gp) {

		super(gp);
		
		setName("City Girl");
		direction = "down";
		setSpeed(BASE_SPEED);
		setBlockSize(GamePanel.TILE_SIZE);
		getImage("npc/zyc");
		
		// Set NPC's dialogues
		setDialogues(new String[]{
			"Hi there! I'm from the city.",
			"This village is so peaceful...",
			"I might stay here forever!"
		});
	}

	@Override
	public void update() {
		// Call parent NPC update to handle random movement
		super.update();
	}
}
