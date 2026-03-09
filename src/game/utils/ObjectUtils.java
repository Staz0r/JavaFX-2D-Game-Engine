package game.utils;

import game.GamePanel;
import object.OBJ_PokeBall;
import object.SuperObject;

public class ObjectUtils {
	
	GamePanel gp;
	public SuperObject[] objects;
	
	public ObjectUtils(GamePanel gp) {

		this.gp = gp;
		this.objects = new SuperObject[10]; // Array to hold objects
		initializeObject();
	}
	
	public void initializeObject() {
		
		objects[0] = new OBJ_PokeBall(gp);
		// Make sure these coordinates are within the game world bounds
		objects[0].setWorldX(30 * GamePanel.TILE_SIZE);
		objects[0].setWorldY(27 * GamePanel.TILE_SIZE);
		objects[1] = new OBJ_PokeBall(gp);
		// Make sure these coordinates are within the game world bounds
		objects[1].setWorldX(8 * GamePanel.TILE_SIZE);
		objects[1].setWorldY(16 * GamePanel.TILE_SIZE);
	}
}
