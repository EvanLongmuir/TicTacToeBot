module EvanLongmuirTikTacToe {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.graphics;
	requires javafx.media;
	
	opens tiktactoe to javafx.graphics, javafx.fxml;
}
