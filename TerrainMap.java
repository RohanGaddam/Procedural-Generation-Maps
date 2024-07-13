import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class TerrainMap extends JPanel {
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
            Color.BLACK, Color.GRAY, new Color(1, 107, 1),  Color.GREEN, Color.BLUE,
            new Color(0, 0, 170), Color.WHITE
    };
    private int types = 7;
    private int[][] notAllowed = {
            {0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 0},
            {0, 0, 0, 0, 1, 1, 1},
            {0, 1, 0, 0, 0, 1, 1},
            {0, 1, 1, 0, 0, 0, 1},
            {0, 1, 1, 1, 0, 0, 1},
            {0, 0, 1, 1, 1, 1, 0}
    };

    public TerrainMap(int width, int height) {
        this.worldWidth = width / cellSize;
        this.worldHeight = height / cellSize;
        this.map = new int[worldWidth][worldHeight];

        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                map[x][y] = UNDECLARED;
            }
        }

        setPreferredSize(new Dimension(width, height));
        Timer timer = new Timer(100, e -> {
            if (!leastConflicts()) {
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                g.setColor(colors[map[x][y]]);
                g.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private boolean leastConflicts() {
        boolean success = true;
        int tries = 8;
        Random rand = new Random();

        for (int i = 0; i < worldWidth * worldHeight; i++) {
            int x = rand.nextInt(worldWidth);
            int y = rand.nextInt(worldHeight);
            int conflicts = checkConflicts(x, y, map[x][y]);

            if (conflicts > 0 || map[x][y] == UNDECLARED) {
                success = false;
                int bestType = UNDECLARED;
                int leastConflicts = Integer.MAX_VALUE;

                for (int j = 0; j < tries; j++) {
                    int tempT = 1 + rand.nextInt(types - 1);
                    int tempC = checkConflicts(x, y, tempT);

                    if (tempC < leastConflicts) {
                        bestType = tempT;
                        leastConflicts = tempC;
                    }
                }

                map[x][y] = bestType;
            }
        }

        return success;
    }

    private int checkConflicts(int x, int y, int type) {
        int conflicts = 0;
        int range = 3;

        for (int dx = -range; dx <= range; dx++) {
            for (int dy = -range; dy <= range; dy++) {
                int tx = (dx + x + worldWidth) % worldWidth;
                int ty = (dy + y + worldHeight) % worldHeight;
                conflicts += notAllowed[type][map[tx][ty]];
            }
        }

        return conflicts;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Procedural Terrain Map");
        TerrainMap terrainMap = new TerrainMap(800, 800);
        frame.add(terrainMap);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
