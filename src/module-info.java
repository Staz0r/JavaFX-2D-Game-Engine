module JavaFX_Project {
	requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.media;
	
	opens application to javafx.graphics, javafx.fxml;
}
