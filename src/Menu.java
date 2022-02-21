import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class Menu extends Pane {
    private HexaGrid hexaGrid;
    private GridPane rootPane;
    private Stage primaryStage;

    private Label sliderLabel;
    private Slider slider;
    private Button btnClear;
    private Button btnPause;
    private Button btnNext;
    private Button btnPrevious;
    private Button btnSave;
    private Button btnLoad;

    /**
     * Costructor create a menu, which consists of basic buttons and slider
     * @param primaryStage
     * @param rootPane
     * @param hexaGrid
     */
    public Menu(Stage primaryStage, GridPane rootPane, HexaGrid hexaGrid) {
        this.hexaGrid = hexaGrid;
        this.rootPane = rootPane;
        this.primaryStage = primaryStage;

        setMaxHeight(100);
        setMinHeight(100);

        setStyle("-fx-background-color: white");

        sliderLabel = new Label("Hexagon side length:");
        sliderLabel.setLayoutX(130);
        getChildren().add(sliderLabel);

        slider = new Slider(5, 75, 25);//25 default
        slider.setLayoutX(125);
        slider.setLayoutY(20);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(25);
        slider.setMinorTickCount(24);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((obs, oldValue, newValue) -> hexaGrid.changeHexagonLength(newValue.doubleValue()));
        slider.setMinWidth(150);
        getChildren().add(slider);

        //clear button
        btnClear = new Button("Clear");
        btnClear.setLayoutX(300);
        btnClear.setOnAction(e -> hexaGrid.clearGrid());
        getChildren().add(btnClear);

        //pause button
        btnPause = new Button();
        btnPause.setGraphic(new ImageView(new Image("icons/play_icon.png")));
        btnPause.setLayoutX(45);
        btnPause.setOnAction(e -> hexaGrid.pauseGrid(btnPause));
        getChildren().add(btnPause);

        //next generation button
        btnNext = new Button();
        btnNext.setGraphic(new ImageView(new Image("icons/next_icon.png")));
        btnNext.setLayoutX(85);
        btnNext.setOnAction(e -> hexaGrid.nextGeneration());
        getChildren().add(btnNext);

        //previous generation button
        btnPrevious = new Button();
        btnPrevious.setGraphic(new ImageView(new Image("icons/previous_icon.png")));
        btnPrevious.setGraphic(new ImageView(new Image("icons/previous_icon.png")));
        btnPrevious.setLayoutX(5);
        btnPrevious.setOnAction(e -> hexaGrid.previousGeneration());
        btnPrevious.setDisable(true);
        getChildren().add(btnPrevious);

        //file saver + button
        FileChooser fileSaver = new FileChooser();
        fileSaver.setTitle("Open Resource File");

        fileSaver.setInitialFileName("SavedHexaGameOfLife.txt");
        fileSaver.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("TXT", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        btnSave = new Button("Save...");
        btnSave.setLayoutX(5+68);
        btnSave.setLayoutY(30);
        btnSave.setOnAction(e -> {
            File file = fileSaver.showSaveDialog(primaryStage);
            hexaGrid.saveToFile(file);
        });
        getChildren().add(btnSave);


        //file loader + button
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("TXT", "*.txt")
        );

        btnLoad = new Button("Load...");
        btnLoad.setLayoutX(5);
        btnLoad.setLayoutY(30);
        btnLoad.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            hexaGrid.pause(btnPause);
            hexaGrid.loadFromFile(file);
        });
        getChildren().add(btnLoad);
        rootPane.widthProperty().addListener((obs, oldValue, newValue) -> hexaGrid.resizeRows(newValue.doubleValue()));
        rootPane.heightProperty().addListener((obs, oldValue, newValue) -> {
            hexaGrid.resizeColumns(newValue.doubleValue());
            setLayoutY(newValue.doubleValue()+100);
        });
    }

    /**
     * enables button for previous generations
     */
    public void enablePreviousBtn() {
        btnPrevious.setDisable(false);
    }

    /**
     * disables button for previous generations
     */
    public void disablePreviousBtn() {
        btnPrevious.setDisable(true);
    }
}
