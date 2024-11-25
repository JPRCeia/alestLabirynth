import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        String filePath = "labTeste/caso40_4.txt";
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
                        countCharacters(grid.getGrid(), characterFrequency);
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

            Result result = countWalkableAreasAndFindMostFrequentCharacter(grids);
            System.out.println("Walkable areas: " + result.walkableAreas);
            System.out.println("Most used character in a single region: " + result.mostUsedCharacter +
                    " (used " + result.maxCount + " times)");

        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Invalid number format in the file: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void countCharacters(int[][] grid, Map<Integer, Integer> frequencyMap) {
        for (int[] row : grid) {
            for (int cell : row) {
                if (cell != 0 && cell != 1) { // Only count characters that are not 0 or 1
                    frequencyMap.put(cell, frequencyMap.getOrDefault(cell, 0) + 1);
                }
            }
        }
    }

    private static Result countWalkableAreasAndFindMostFrequentCharacter(int[][][][] grids) {
        int rows = grids.length;
        int cols = grids[0].length;
        boolean[][] visited = new boolean[rows * 3][cols * 3];
        int areas = 0;
        int maxCount = 0;
        int mostUsedCharacter = -1;

        for (int i = 0; i < rows * 3; i++) {
            for (int j = 0; j < cols * 3; j++) {
                if (isWalkable(grids, i, j) && !visited[i][j]) {
                    Map<Integer, Integer> regionFrequency = new HashMap<>();
                    floodFill(grids, visited, i, j, regionFrequency);
                    areas++;

                    for (Map.Entry<Integer, Integer> entry : regionFrequency.entrySet()) {
                        if (entry.getValue() > maxCount) {
                            maxCount = entry.getValue();
                            mostUsedCharacter = entry.getKey();
                        }
                    }
                }
            }
        }

        return new Result(areas, mostUsedCharacter, maxCount);
    }

    private static void floodFill(int[][][][] grids, boolean[][] visited, int x, int y, Map<Integer, Integer> regionFrequency) {
        int rows = grids.length * 3;
        int cols = grids[0].length * 3;
        if (x < 0 || x >= rows || y < 0 || y >= cols || !isWalkable(grids, x, y) || visited[x][y]) {
            return;
        }

        visited[x][y] = true;

        int cellValue = getCellValue(grids, x, y);
        if (cellValue != 0 && cellValue != 1) {
            regionFrequency.put(cellValue, regionFrequency.getOrDefault(cellValue, 0) + 1);
        }

        floodFill(grids, visited, x + 1, y, regionFrequency);
        floodFill(grids, visited, x - 1, y, regionFrequency);
        floodFill(grids, visited, x, y + 1, regionFrequency);
        floodFill(grids, visited, x, y - 1, regionFrequency);
    }

    private static boolean isWalkable(int[][][][] grids, int x, int y) {
        int gridX = x / 3;
        int gridY = y / 3;
        int subX = x % 3;
        int subY = y % 3;
        return grids[gridX][gridY][subX][subY] != 1;
    }

    private static int getCellValue(int[][][][] grids, int x, int y) {
        int gridX = x / 3;
        int gridY = y / 3;
        int subX = x % 3;
        int subY = y % 3;
        return grids[gridX][gridY][subX][subY];
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
                default: return null;
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

    private static class Result {
        int walkableAreas;
        int mostUsedCharacter;
        int maxCount;

        Result(int walkableAreas, int mostUsedCharacter, int maxCount) {
            this.walkableAreas = walkableAreas;
            this.mostUsedCharacter = mostUsedCharacter;
            this.maxCount = maxCount;
        }
    }
}