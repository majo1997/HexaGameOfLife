import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.util.Duration;

import java.io.*;
import java.util.LinkedList;

public class HexaGrid extends Pane {
    private boolean pause;
    private Timeline animation;
    private Menu menu;

    private int rows;
    private int columns;

    private final double HEXAGON_LENGTH_DEFAULT = 25;
    private final int NUMBER_OF_ACTIVE_STATES = 10;
    private final double MAXIMUM_GRAY_SHADE = 0.5; //0 - black, 1 - white
    private final int REPEAT_TIME_MS = 500;
    private final int OLD_GENERATIONS_CAPACITY = 20;
    private final double ACTIVE_CELLS_TO_SURVIVE = 2;

    private final double MENU_HEIGHT = 100;

    private double hexagonLength;
    private Cell[][] cells;

    private LinkedList<Cell[][]> oldGenerations = new LinkedList<>();

    private class Cell {
        private int row;
        private int col;

        private int state;

        private double centerX;
        private double centerY;

        private Polyline hexagon;

        /**
         * Cell constructor
         * @param row cell row index
         * @param col cell column index
         * @param value cell state value
         */
        public Cell(int row, int col, int value) {
            this.row = row;
            this.col = col;

            this.state = value;

            double halfWidth = hexagonLength * Math.cos(Math.PI / 6);
            this.centerX = (row % 2 == 0) ? (2 * col + 1) * halfWidth : (2 * col + 2) * halfWidth;
            this.centerY = (1.5 * row + 1) * hexagonLength;

            createHexagon();
        }

        /**
         * Cell copy constructor
         * @param cell cell to get values from
         */
        public Cell(Cell cell) {
            this.row = cell.row;
            this.col = cell.col;

            this.state = cell.state;

            this.centerX = cell.centerX;
            this.centerY = cell.centerY;

            this.hexagon = cell.hexagon;
        }

        /**
         * Getter of state attribute
         *
         * @return  state of cell
         */
        public int getState() {
            return state;
        }

        /**
         * Setter of state attribute used in need to change cell state.
         *
         * @param state new cell state
         */
        public void setState(int state) {
            this.state = state;
        }

        /**
         * This method calculate and return 2D array of points coordinates for hexagon.
         *
         * @return  returns 2D array of points
         */
        public double[][] getHexagonPoints() {
            double[][] points = new double[2][6];

            for (int i = 0; i < 6; i++) {
                double alpha = Math.PI / Math.PI * ((Math.PI / 3) * i - (Math.PI / 6));
                points[0][i] = centerX + hexagonLength * Math.cos(alpha);
                points[1][i] = centerY + hexagonLength * Math.sin(alpha);
            }

            return points;
        }

        /**
         * This method create hexagon for cell and adds it to hexagon grid
         */
        public void createHexagon() {
            double[][] points = getHexagonPoints();

            hexagon = new Polyline(
                    points[0][0], points[1][0],
                    points[0][1], points[1][1],
                    points[0][2], points[1][2],
                    points[0][3], points[1][3],
                    points[0][4], points[1][4],
                    points[0][5], points[1][5],
                    points[0][0], points[1][0]
            );

            hexagon.setStroke(Color.BLACK);

            HexaGrid.this.getChildren().add(hexagon);
        }

        /**
         * This method paint the hexagon of cell.
         */
        public void paint() {
            // calculate correct color for cell
            if (isAlive()) {
                double colorRGB = MAXIMUM_GRAY_SHADE / NUMBER_OF_ACTIVE_STATES;
                colorRGB = (state - 1) * colorRGB;

                Color c = Color.color(colorRGB, colorRGB, colorRGB);
                hexagon.setFill(c);
            }
            else {
                hexagon.setFill(Color.WHITE);
            }

        }

        /**
         * Method changes hexagon side length
         */
        public void changeHexagonLength() {
            double halfWidth = hexagonLength * Math.cos(Math.PI / 6);
            this.centerX = (row % 2 == 0) ? (2 * col + 1) * halfWidth : (2 * col + 2) * halfWidth;

            this.centerY = (1.5 * row + 1) * hexagonLength;

            getChildren().remove(hexagon);
            createHexagon();
        }


        /**
         * Set the cell state to active
         */
        public void activate() {
            if (state != NUMBER_OF_ACTIVE_STATES) state++;
        }

        /**
         * Method to deactivate cell.
         */
        public void deactivate() {
            state = 0;
        }

        /**
         * @return  whether or not is the cell active
         */
        public boolean isAlive() {
            return state != 0;
        }

        /**
         * Method change the state of cell to opposite.
         */
        public void changeStatus() {
            state = (state == 0) ? 1 : 0;
        }
    }

    /**
     * Class constructor for hexagon grid
     * @param rows number of rows to create
     * @param columns number of columns to create
     */
    public HexaGrid(int rows, int columns) {
        hexagonLength = HEXAGON_LENGTH_DEFAULT;

        this.rows = rows;
        this.columns = columns;

        setStyle("-fx-background-color: white");

        cells = new Cell[rows][columns];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                cells[row][col] = new Cell(row, col, 0);
            }
        }

        pause = true;
        EventHandler<ActionEvent> eventHandler = e -> nextGeneration();

        animation = new Timeline(new KeyFrame(Duration.millis(REPEAT_TIME_MS), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);

        setOnMouseClicked(this::selectHexagon);

        paint();
    }

    private void selectHexagon(MouseEvent event) {
        //if paused, then can not activate/deactivate cell
        if (!pause) {
            return;
        }

        int row = 0;
        int col = 0;

        double halfWidth = hexagonLength * Math.cos(Math.PI / Math.PI * (Math.PI / 6));
        double middleHeight = 2 * Math.abs(hexagonLength * Math.sin(Math.PI / Math.PI * (Math.PI / 6)));//ASI ZMAZAT MATHPI/MATHPI vsade"""""!!!
        double upperHeight = (2 * hexagonLength - middleHeight) / 2;


        int halfCol = (int) Math.floor(event.getX() / halfWidth);
        double x = event.getX() - (halfCol * halfWidth);
        double y = event.getY();


        int type;

        if (halfCol % 2 == 0) type = 0;
        else type = 1;

        while (y >= 0) {
            if (y <= upperHeight) {
                if (type == 0 && halfCol - 1 >= 0) {
                    col = (halfCol - 1) / 2;
                } else if (type == 1 && halfCol >= 0) {
                    col = halfCol / 2;
                } else {
                    col = -1;
                }

                if (type == 0) {
                    x = halfWidth - x;
                }

                Triangle t = new Triangle(
                        0, upperHeight,
                        halfWidth, upperHeight,
                        halfWidth, 0,
                        x, upperHeight - y
                );

                if (t.contains()) {
                    if (type == 0) {
                        row--;
                    } else if (type == 1) {
                        row--;
                    }
                } else {
                    if (row % 2 == 1) {
                        col--;
                    }
                    if (type == 0) {
                        col++;
                    }

                }

                break;
            } else {
                type = (type == 0) ? 1 : 0;
                y -= upperHeight;
            }

            if (y <= middleHeight) {
                if (type == 0 && halfCol - 1 >= 0) {
                    col = (halfCol - 1) / 2;
                } else if (type == 1 && halfCol >= 0) {
                    col = halfCol / 2;
                } else {
                    col = -1;
                }
                break;
            } else {
                y -= middleHeight;
                row++;
            }

        }

        if (col >= 0 && col < columns && row >= 0 && row < rows) {
            cells[row][col].changeStatus();
            cells[row][col].paint();
        }
    }

    /**
     * Method changes the hexagon length for each hexagon in grid
     * @param newLength length to change to
     */
    public void changeHexagonLength(double newLength) {
        hexagonLength = newLength;

        double halfWidth = hexagonLength * Math.cos(Math.PI / Math.PI * (Math.PI / 6));

        int newNumberOfColumns = (int)((getWidth() - halfWidth) / (2 * halfWidth));

        resizeGrid(rows, newNumberOfColumns);

        double middleHeight = 2 * Math.abs(hexagonLength * Math.sin(Math.PI / Math.PI * (Math.PI / 6)));
        double upperHeight = (2 * hexagonLength - middleHeight) / 2;

        int newNumberOfRows = (int)((getHeight() - upperHeight) / (middleHeight + upperHeight));

        resizeGrid(newNumberOfRows, columns);

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                cells[r][c].changeHexagonLength();
            }
        }

        paint();
    }

    /**
     * Method change rows number
     * @param newWidth new width of hexagon grid in pixels
     */
    public void resizeRows(double newWidth) {
        double halfWidth = hexagonLength * Math.cos(Math.PI / Math.PI * (Math.PI / 6));

        int newNumberOfColumns = (int)((newWidth - halfWidth) / (2 * halfWidth));

        setMinWidth(newWidth);

        resizeGrid(rows, newNumberOfColumns);
    }

    /**
     * Method change columns number
     * @param newHeight new height of hexagon grid in pixels
     */
    public void resizeColumns(double newHeight) {
        double middleHeight = 2 * Math.abs(hexagonLength * Math.sin(Math.PI / Math.PI * (Math.PI / 6)));
        double upperHeight = (2 * hexagonLength - middleHeight) / 2;

        setMinHeight(newHeight-MENU_HEIGHT);
        setMaxHeight(newHeight-MENU_HEIGHT);

        int newNumberOfRows = (int)((newHeight - upperHeight - MENU_HEIGHT) / (middleHeight + upperHeight));

        resizeGrid(newNumberOfRows, columns);
    }

    /**
     * Method resize grid to selected rows and columns
     * @param rows      rows of grid after resize
     * @param columns   columns of grid after resize
     */
    private void resizeGrid(int rows, int columns) {
        int oldRows = this.rows;
        int oldColumns = this.columns;
        this.rows = rows;
        this.columns = columns;

        Cell[][] oldCells = cells;
        cells = new Cell[rows][columns];

        for(int r = 0; r < rows; r++) {
            for(int c = columns; c < oldColumns; c++) {
                getChildren().remove(oldCells[r][c].hexagon);
            }
        }

        for(int r = rows; r < oldRows; r++) {
            for(int c = 0; c < columns; c++) {
                getChildren().remove(oldCells[r][c].hexagon);
            }
        }

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {

                if(r < oldRows && c < oldColumns) {
                    cells[r][c] = oldCells[r][c];
                }
                else {
                    cells[r][c] = new Cell(r, c, 0);
                }
            }
        }

        paint();
    }

    /**
     * Create new generation of cells and replace the old ones.
     */
    public void nextGeneration() {
        Cell[][] newGeneration = new Cell[rows][columns];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (activeNeighbours(r, c) == ACTIVE_CELLS_TO_SURVIVE) {
                    newGeneration[r][c] = new Cell(cells[r][c]);
                    newGeneration[r][c].activate();
                } else {
                    newGeneration[r][c] = new Cell(cells[r][c]);
                    newGeneration[r][c].deactivate();
                }
            }
        }

        //save previous generations
        oldGenerations.addLast(cells);
        if(oldGenerations.size() > OLD_GENERATIONS_CAPACITY) {
            oldGenerations.removeFirst();
        }

        cells = newGeneration;

        menu.enablePreviousBtn();

        paint();
    }

    /**
     * This method adds instance of menu
     * @param menu  instance of menu to add
     */
    public void addMenu(Menu menu) {
        this.menu = menu;
    }

    /**
     * Back to previous generation
     */
    public void previousGeneration() {
        System.out.println(rows+","+columns);
        Cell[][] previousGenerationCells = oldGenerations.removeLast();

        setGrid(previousGenerationCells);//ak je nasledujuca mriezka vacsia tak ju zobere inak nie

        if(oldGenerations.size() <= 0) menu.disablePreviousBtn();

        paint();
    }

    private void setGrid(Cell[][] cells) {
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (r >= cells.length || c >= cells[r].length) {
                    this.cells[r][c].setState(0);//off
                    continue;
                }

                this.cells[r][c].setState(cells[r][c].getState());
            }
        }
    }

    /**
     * Returns state whether selected cell is active.
     *
     * @param row   row of selected cell
     * @param col   column of selected cell
     * @return      boolean value whether cell is active
     */
    public boolean isCellAlive(int row, int col) {
        try {
            return cells[row][col].isAlive();
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Returns number of active neighbours of selected cell.
     *
     * @param row   row of selected cell
     * @param col   column of selected cell
     * @return      number of active neighbours
     */
    public int activeNeighbours(int row, int col) {
        int count = 0;

        //for even column
        if (row % 2 == 0) {
            if (isCellAlive(row - 1, col)) count++;
            if (isCellAlive(row - 1, col - 1)) count++;
            if (isCellAlive(row, col - 1)) count++;
            if (isCellAlive(row, col + 1)) count++;
            if (isCellAlive(row + 1, col)) count++;
            if (isCellAlive(row + 1, col - 1)) count++;
        }
        //for odd column
        else {
            if (isCellAlive(row - 1, col)) count++;
            if (isCellAlive(row - 1, col + 1)) count++;
            if (isCellAlive(row, col - 1)) count++;
            if (isCellAlive(row, col + 1)) count++;
            if (isCellAlive(row + 1, col)) count++;
            if (isCellAlive(row + 1, col + 1)) count++;
        }

        return count;
    }

    /**
     * this method pause the grid timer and change the pause/play button
     * @param btnPause button to change the state of
     */
    public void pauseGrid(Button btnPause) {
        if (pause) {
            animation.play();
            btnPause.setGraphic(new ImageView(new Image("icons/pause_icon.png")));
            pause = false;
        } else {
            animation.stop();
            btnPause.setGraphic(new ImageView(new Image("icons/play_icon.png")));
            pause = true;
        }
    }

    /**
     * Repaint the whole grid hexagons
     */
    public void paint() {
        for (Cell[] line : cells) {
            for (Cell cell : line) {
                cell.paint();
            }
        }
    }

    /**
     * This method saves actual grid to file
     * @param f selected file
     */
    public void saveToFile(File f) {
        if (f == null) return;

        try {
            f.createNewFile();

            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write(oldGenerations.size()+"");
            bw.newLine();
            bw.write(rows + " " + columns);
            bw.newLine();

            for (Cell[] line : cells) {
                for (Cell cell : line) {
                    bw.write(cell.getState() + " ");
                }
                bw.newLine();
            }

            //previous generations
            for(Cell[][] generation: oldGenerations) {
                bw.write(generation.length + " " + generation[0].length);
                bw.newLine();
                for (Cell[] line : generation) {
                    for (Cell cell : line) {
                        bw.write(cell.getState() + " ");
                    }
                    bw.newLine();
                }
            }

            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method loads grid from file
     * @param f selected file
     */
    public void loadFromFile(File f) {
        if (f == null) return;

        try {
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);

            String firstLine = br.readLine();
            int numberOfGenerations = Integer.parseInt(firstLine);

            firstLine = br.readLine();
            String[] params = firstLine.split(" ");
            int rows = Integer.parseInt(params[0]);
            int columns = Integer.parseInt(params[1]);

            for (int r = 0; r < this.rows; r++) {
                if(r < rows)
                    params = br.readLine().split(" ");

                for (int c = 0; c < this.columns; c++) {
                    if(r >= rows || c >= columns) {
                        cells[r][c].setState(0);
                    }
                    else
                        cells[r][c].setState(Integer.parseInt(params[c]));
                }
            }

            oldGenerations.clear();
            for(int i = 0; i < numberOfGenerations; i++) {
                firstLine = br.readLine();
                params = firstLine.split(" ");
                rows = Integer.parseInt(params[0]);
                columns = Integer.parseInt(params[1]);

                Cell[][] generation = new Cell[this.rows][this.columns];
                for (int r = 0; r < this.rows; r++) {
                    if(r < rows)
                        params = br.readLine().split(" ");

                    for (int c = 0; c < this.columns; c++) {
                        generation[r][c] = new Cell(cells[r][c]);

                        if(r >= rows || c >= columns) {
                            generation[r][c].setState(0);
                        }
                        else {
                            generation[r][c].setState(Integer.parseInt(params[c]));
                        }
                    }
                }

                oldGenerations.addLast(generation);
            }
            System.out.println(oldGenerations.size());

            if(oldGenerations.size() > 0)
                menu.enablePreviousBtn();
            else
                menu.disablePreviousBtn();

            paint();

            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pause the simulation
     * @param btnPause  button to change the state of
     */
    public void pause(Button btnPause) {
        animation.stop();
        btnPause.setGraphic(new ImageView(new Image("icons/play_icon.png")));
        pause = true;
    }

    /**
     * Clears the grid (deactivate all active cells) and delete previous generations
     */
    public void clearGrid() {
        oldGenerations.clear();
        menu.disablePreviousBtn();

        for(int r = 0; r < rows; r++) {
            for(int c = 0; c < columns; c++) {
                cells[r][c].setState(0);
            }
        }

        paint();
    }
}