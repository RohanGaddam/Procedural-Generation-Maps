import javax.swing.*;
import java.awt.*;
import java.util.Random;

class TerrainMap extends JPanel {
    private static final int UNDECLARED = 0;
    private static final int MOUNTAINS = 1;
    private static final int FOREST = 2;
    private static final int PLAINS = 3;
    private static final int WATER = 4;
    private static final int DEEPWATER = 5;
    private static final int HIGH_MOUNTAINS = 6;

    private int[][] map;
    private int worldWidth, worldHeight;
    private int cellSize = 5;
    private Color[] colors = {
            Color.BLACK,                    // undeclared
            Color.GRAY,                     // mountains
            new Color(1, 107, 1),  // forest
            Color.GREEN,                    // plains
            Color.BLUE,                     // water
            new Color(0, 0, 170),  // deep water
            Color.WHITE                     // high mountains
    };
    private int types = 7;

    // 1 if will cause a conflict 0 if won't cause a conflict
    private int[][] notAllowed = {
            {0, 0, 0, 0, 0, 0, 0}, // undeclared can be next to anything
            {0, 0, 0, 1, 1, 1, 0}, // mountains can't be next to plains, water, or deep water
            {0, 0, 0, 0, 1, 1, 1}, // forest can't be next to water, deep water, or high mountains
            {0, 1, 0, 0, 0, 1, 1}, // plains can't be next to mountains, deep water, or high mountains
            {0, 1, 1, 0, 0, 0, 1}, // water can't be next to mountains, forest, or high mountains
            {0, 1, 1, 1, 0, 0, 1}, // deep water can't be next to mountains, forest, plains, or high mountains
            {0, 0, 1, 1, 1, 1, 0}  // high mountains can't be next to forest, plains, water, or deep water
    };
    private boolean[][] fixedCells; // cells the user predetermines

    public TerrainMap(int width, int height) {
        this.worldWidth = width / cellSize;
        this.worldHeight = height / cellSize;
        this.map = new int[worldWidth][worldHeight];
        this.fixedCells = new boolean[worldWidth][worldHeight];

        // create empty map
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                map[x][y] = UNDECLARED;
            }
        }

        setPreferredSize(new Dimension(width, height));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                g.setColor(colors[map[x][y]]);
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    public void startGeneration() {
        Timer timer = new Timer(100, e -> {
            if (!leastConflicts()) {
                repaint();
            }
        });
        timer.start();
    }


    public int getCellSize() {
        return cellSize;
    }

    public void setCellType(int x, int y, int type) {
        map[x][y] = type;
        fixedCells[x][y] = true; // Mark this cell as fixed
    }

    /**
     * gets rid of conflicts in the map
     *
     * @return true if no conflicts found and false if conflicts found
     */
    private boolean leastConflicts() {
        boolean success = true;
        int tries = 8; // probably try each terrain type once since 7 types
        Random rand = new Random();

        // check random cells to avoid any biases in generation
        for (int i = 0; i < worldWidth * worldHeight; i++) {
            int x = rand.nextInt(worldWidth);
            int y = rand.nextInt(worldHeight);

            // skip fixed cells
            if (fixedCells[x][y]) continue;

            int conflicts = checkConflicts(x, y, map[x][y]);
            if (conflicts > 0 || map[x][y] == UNDECLARED) {
                // try random terrain types and use the one that has the lesat conflicts
                success = false;
                int bestType = UNDECLARED;
                int leastConflicts = 100; // large number

                for (int j = 0; j < tries; j++) {
                    int tempT = 1 + rand.nextInt(types - 1); // temporary random type
                    int tempC = checkConflicts(x, y, tempT); // temporary num conflicts

                    if (tempC < leastConflicts) {
                        bestType = tempT;
                        leastConflicts = tempC;
                    }
                }
                map[x][y] = bestType; // fill in cell with type that has the least conflicts
            }
        }
        return success;
    }

    /**
     * checks for conflicts around the specified cell
     * @param x x coordinate of cell
     * @param y y coordinate of cell
     * @param type terrain type of cell
     * @return number of conflicts found around cell
     */
    private int checkConflicts(int x, int y, int type) {
        int conflicts = 0;
        int range = 3; // how far away to check from cell if there is a conflict

        // loop through
        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int tx = (dx + x + worldWidth) % worldWidth; // wrap around map if at edge
                int ty = (dy + y + worldHeight) % worldHeight; // wrap around map if at edge

                // checks table if position at tx,ty has conflicts with current type and adds a conflict if there is one
                conflicts += notAllowed[type][map[tx][ty]];
            }
        }

        return conflicts;
    }
}
