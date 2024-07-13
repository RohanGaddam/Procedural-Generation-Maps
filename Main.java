import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Procedural Terrain Map");
        TerrainMap terrainMap = new TerrainMap(800, 1000);
        frame.add(terrainMap);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
