package application;
	
import game.GamePanel;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			GamePanel gamePanel = new GamePanel();
			
			// Disable anti-aliasing
			gamePanel.getCanvas().getGraphicsContext2D().setImageSmoothing(false);
			
			StackPane root = new StackPane(gamePanel.getCanvas());
			
			Scene scene = new Scene(root, GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT);
			
			scene.setOnKeyPressed(e -> gamePanel.keyH.keyPressed(e));
			scene.setOnKeyReleased(e -> gamePanel.keyH.keyReleased(e));
			
			primaryStage.setTitle("Pokemon RipOff 2.0");
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/game/game_icon.png")));
			primaryStage.setResizable(false);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}