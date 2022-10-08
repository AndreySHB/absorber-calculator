package main.java.model;

import java.util.Arrays;
/**Spherically symmetrical grid 2 store position and amount of absorbed photons*/
public class Grid {

    private final int rMax;

    private final int zMax;
    private final Cell[][] cells;


    public Grid(int rMax, int zMax) {
        this.rMax = rMax;
        this.zMax = zMax;
        cells = new Cell[rMax][zMax];

        for (int i = 0; i < zMax; i++) {
            for (int j = 0; j < rMax; j++) {
                cells[j][i] = new Cell();
            }
        }
    }

    public void accumulate(int r, int z) {
        getCell(r, z).add();
    }

    public long calcAccumPhotons() {
        return Arrays.stream(cells)
                .parallel()
                .flatMap(Arrays::stream)
                .map(Cell::getAccumulated).reduce(0, Integer::sum);
    }

    public int[][] getAccumArray() {
        int[][] array = new int[rMax][zMax];
        for (int i = 0; i < zMax; i++) {
            for (int j = 0; j < rMax; j++) {
                array[j][i] = cells[j][i].getAccumulated();
            }
        }
        return array;
    }

    public boolean isNotInBorders(int r, int z) {
        return rMax <= r || zMax <= z
                || r < 0 || z < 0;
    }

    private Cell getCell(int r, int z) {
        return cells[r][z];
    }

    public static class Cell {
        private int accumulated;

        public int getAccumulated() {
            return accumulated;
        }

        public synchronized void add() {
            accumulated++;
        }
    }
}
