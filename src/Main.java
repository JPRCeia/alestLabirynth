import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String filePath = "labTeste/caso4_4.txt";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String sizeLine = reader.readLine();
            if (sizeLine == null) {
                throw new IllegalArgumentException("File is empty!");
            }

            String[] sizeParts = sizeLine.split("\\s+");
            int rows = Integer.parseInt(sizeParts[0]);
            int cols = Integer.parseInt(sizeParts[1]);

            List<String[]> gridValues = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) {
                gridValues.add(line.split("\\s+"));
            }

            if (gridValues.size() != rows || gridValues.get(0).length != cols) {
                throw new IllegalArgumentException("Matrix size in the file doesn't match the specified dimensions!");
            }

            int[][][][] grids = new int[rows][cols][3][3];
            Map<Integer, Integer> characterFrequency = new HashMap<>();

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    String value = gridValues.get(i)[j];
                    Grid grid = getGridByValue(value);
                    if (grid != null) {
                        grids[i][j] = grid.getGrid();
                        countCharacters(grid.getGrid(), characterFrequency); // Count characters here
                    } else {
                        System.out.println("Invalid grid value: " + value);
                        grids[i][j] = new int[][] {
                                {0, 0, 0},
                                {0, 0, 0},
                                {0, 0, 0}
                        };
                    }
                }
            }

            for (int i = 0; i < rows; i++) {
                for (int subRow = 0; subRow < 3; subRow++) {
                    for (int j = 0; j < cols; j++) {
                        for (int cell : grids[i][j][subRow]) {
                            System.out.print(cell == 1 ? "#" :
                                    cell == 2 ? "A" :
                                            cell == 3 ? "B" :
                                                    cell == 4 ? "C" :
                                                            cell == 5 ? "D" :
                                                                    cell == 6 ? "E" :
                                                                            cell == 7 ? "F" : ".");
                        }
                        System.out.print(" ");
                    }
                    System.out.println();
                }
            }

            // Count walkable areas
            int walkableAreas = countWalkableAreas(grids);
            System.out.println("Walkable areas: " + walkableAreas);

            // Find and print the most used character
            int mostUsedCharacter = findMostFrequentCharacter(characterFrequency);
            System.out.println("Most used character: " + mostUsedCharacter +
                    " (used " + characterFrequency.get(mostUsedCharacter) + " times)");

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Helper method to count characters in a single grid
    private static void countCharacters(int[][] grid, Map<Integer, Integer> frequencyMap) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell != 0 && cell != 1) { // Only count characters that are not 0 or 1
                    frequencyMap.put(cell, frequencyMap.getOrDefault(cell, 0) + 1);
                }
            }
        }
    }

    // Helper method to find the most frequent character
    private static int findMostFrequentCharacter(Map<Integer, Integer> frequencyMap) {
        int mostUsedCharacter = -1;
        int maxCount = 0;

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxCount) {
                mostUsedCharacter = entry.getKey();
                maxCount = entry.getValue();
            }
        }

        return mostUsedCharacter;
    }

    // Helper method to count walkable areas using flood fill
    private static int countWalkableAreas(int[][][][] grids) {
        int rows = grids.length;
        int cols = grids[0].length;
        boolean[][] visited = new boolean[rows * 3][cols * 3];
        int areas = 0;

        for (int i = 0; i < rows * 3; i++) {
            for (int j = 0; j < cols * 3; j++) {
                if (isWalkable(grids, i, j) && !visited[i][j]) {
                    floodFill(grids, visited, i, j);
                    areas++;
                }
            }
        }

        return areas;
    }

    private static void floodFill(int[][][][] grids, boolean[][] visited, int x, int y) {
        int rows = grids.length * 3;
        int cols = grids[0].length * 3;
        if (x < 0 || x >= rows || y < 0 || y >= cols || !isWalkable(grids, x, y) || visited[x][y]) {
            return;
        }

        visited[x][y] = true;

        floodFill(grids, visited, x + 1, y);
        floodFill(grids, visited, x - 1, y);
        floodFill(grids, visited, x, y + 1);
        floodFill(grids, visited, x, y - 1);
    }

    private static boolean isWalkable(int[][][][] grids, int x, int y) {
        int gridX = x / 3;
        int gridY = y / 3;
        int subX = x % 3;
        int subY = y % 3;
        return grids[gridX][gridY][subX][subY] != 1;
    }

    public static Grid getGridByValue(String value) {
        try {
            int number = Integer.parseInt(value);
            switch (number) {
                case 1: return Grid.ONE;
                case 2: return Grid.TWO;
                case 3: return Grid.THREE;
                case 4: return Grid.FOUR;
                case 5: return Grid.FIVE;
                case 6: return Grid.SIX;
                case 7: return Grid.SEVEN;
                case 8: return Grid.EIGHT;
                case 9: return Grid.NINE;
                default: return null; // Invalid number
            }
        } catch (NumberFormatException e) {
            switch (value) {
                case "a": return Grid.a;
                case "b": return Grid.b;
                case "c": return Grid.c;
                case "d": return Grid.d;
                case "e": return Grid.e;
                case "f": return Grid.f;
                case "A": return Grid.A;
                case "B": return Grid.B;
                case "C": return Grid.C;
                case "D": return Grid.D;
                case "E": return Grid.E;
                case "F": return Grid.F;
                default: return null;
            }
        }
    }
}