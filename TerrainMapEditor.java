import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TerrainMapEditor extends JFrame {
    private TerrainMap terrainMap;
    private int selectedType = 0; // default undeclared
    private JButton[] buttons;

    public TerrainMapEditor() {
        setTitle("Terrain Map Editor");
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create empty terrain map
        terrainMap = new TerrainMap(600, 800);
        add(terrainMap, BorderLayout.CENTER);

        // side panel for buttons
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new GridLayout(8, 1));
        add(sidePanel, BorderLayout.EAST);

        // add buttons
        String[] labels = {"Undeclared", "High Mountains", "Mountains", "Forest", "Plains", "Water", "Deepwater"};
        Color[] buttonColors = {
                Color.BLACK, Color.WHITE, Color.GRAY, new Color(1, 107, 1),
                Color.GREEN, Color.BLUE, new Color(0, 0, 170)
        };
        int[] types = {0, 6, 1, 2, 3, 4, 5}; // different order so that buttons make more sense
        buttons = new JButton[labels.length];
        for (int i = 0; i < labels.length; i++) {
            buttons[i] = createTerrainButton(labels[i], buttonColors[i], types[i]);
            sidePanel.add(buttons[i]);
        }

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> {
            // start generating terrain after start button pressed
            terrainMap.startGeneration();
        });
        sidePanel.add(startButton);

        // mouse listener for terrain map
        terrainMap.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / terrainMap.getCellSize();
                int y = e.getY() / terrainMap.getCellSize();
                terrainMap.setCellType(x, y, selectedType);
                terrainMap.repaint();
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * creates a button for selecting terrain types.
     *
     * @param label button label
     * @param color button background color
     * @param type  button terrain type
     * @return created JButton
     */
    private JButton createTerrainButton(String label, Color color, int type) {
        JButton button = new JButton(label);
        button.setBackground(color);
        button.setOpaque(true);
        button.addActionListener(e -> selectedType = type);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TerrainMapEditor());
    }
}
