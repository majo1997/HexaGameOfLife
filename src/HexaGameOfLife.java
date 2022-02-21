import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class HexaGameOfLife extends Application {

	@Override
	public void start(Stage primaryStage) {
		GridPane rootPane = new GridPane();
		HexaGrid hg = new HexaGrid(10, 10);

		Menu menu = new Menu(primaryStage, rootPane, hg);
		hg.addMenu(menu);

		rootPane.add(hg, 0, 0);
		rootPane.add(menu, 0, 1);
		Scene scene = new Scene(rootPane);

		primaryStage.setScene(scene);
		primaryStage.getIcons().add(new Image("icons/hexagons_icon.png"));
		primaryStage.setTitle("Hexa Game of Life");
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}