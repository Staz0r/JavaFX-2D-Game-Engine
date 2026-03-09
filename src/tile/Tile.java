package tile;

import javafx.scene.image.Image;

public class Tile {
	
	public Image image;
	protected boolean collision = false;

	public boolean getCollision() {

        return this.collision;
    }

	public void setCollision(boolean flag) {

		this.collision = flag;
	}
}