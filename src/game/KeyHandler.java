package game;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;

public class KeyHandler {
	
	public boolean shiftPressed, enterPressed, escapePressed;
	public boolean upPressed, downPressed, leftPressed, rightPressed, anyPressed;
	public boolean isDebugMode, interactPressed;
	
	public void keyPressed(KeyEvent e) {
        
		KeyCode code = e.getCode();
		
        if (code == KeyCode.W || e.getCode() == KeyCode.UP) {
            upPressed = true;
        } 
        if (code == KeyCode.S || e.getCode() == KeyCode.DOWN) {
            downPressed = true;
        }
        if (code == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
            rightPressed = true;
        }
        if (code == KeyCode.A || e.getCode() == KeyCode.LEFT) {
            leftPressed = true;
        }
        // Debug key
		if (code == KeyCode.F3) {
			isDebugMode = !isDebugMode;
		}
		
		// Pause/Menu key
		if (code == KeyCode.ESCAPE) {
			escapePressed = true;
		}
		
		if (code == KeyCode.SHIFT) {
			shiftPressed = true;
		}
		
		// Confirmation key
		if (code == KeyCode.ENTER) {
			enterPressed = true;
		}
		
		// Interact key
		if (code == KeyCode.F) {
		    interactPressed = true;
		}
        
        anyPressed = upPressed || downPressed || rightPressed || leftPressed;
	}

	public void keyReleased(KeyEvent e) {
		
		KeyCode code = e.getCode();
		
		if (code == KeyCode.W || e.getCode() == KeyCode.UP) {
			upPressed = false;
		} 
		if (code == KeyCode.S || e.getCode() == KeyCode.DOWN) {
			downPressed = false;
		}
		if (code == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
			rightPressed = false;
		}
		if (code == KeyCode.A || e.getCode() == KeyCode.LEFT) {
			leftPressed = false;
		}
		if (code == KeyCode.ENTER) {
			enterPressed = false;
		}
		if (code == KeyCode.SHIFT) {
			shiftPressed = false;
		}
		if (code == KeyCode.ESCAPE) {
			escapePressed = false;
		}
		if (code == KeyCode.F) {
			interactPressed = false;
		}
		
		anyPressed = upPressed || downPressed || rightPressed || leftPressed;
	}
}
