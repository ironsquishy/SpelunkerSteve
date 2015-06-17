package com.games.spelunkersteve.mechanics;

import java.util.Random;

public class Lattice {
    int height;
    int width;
    final int RIGHT = 0;
    final int UP = 1;
    final int DOWN  = 2;
    final int BRANCH = 3;
    char[][] lattice;
    
    public Lattice() {
    	this.height = 10;
    	this.width = 100;
    	initLattice();
    }
    
    public Lattice(int height, int width) {
    	this.height = height;
    	this.width = width;
    	initLattice();
    }
    
    private char[] generateColumn(char[] previousColumn, int difficulty) {
        char[] column = new char[height];
        column[0] = '-';
        column[height - 1] = '-';
        if (previousColumn == null) {
            for (int i = 1; i < height - 1; i++) {
                column[i] = '-';
                if (i == 2) {
                    column[i] = 'U';
                }
                if (i == 3) {
                    column[i] = 'R';
                }
                if (i == 4) {
                    column[i] = 'D';
                }
            }
        } else {
            for (int i = 0; i < height; i++) {
                if (i == 0 || i == height - 1) {
                    if (i == 0) {
                        if (previousColumn[i] == 'D') {
                            column[i + 1] = generateMovement();
                            column[i] = 'X';
                            previousColumn[i + 1] = '-';
                        }
                        if (previousColumn[i] == 'U' || previousColumn[i] == 'B') {
                            column[i] = 'D';
                        }
                        if (previousColumn[i] == 'R') {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == 'X') {
                            column[i] = '-';
                        }
                        if (column[i] == 0) {
                            column[i] = '-';
                        }
                    }
                    if (i == height - 1) {
                        if (previousColumn[i] == 'D' || previousColumn[i] == 'B') {
                            column[i] = 'U';
                        }
                        if (previousColumn[i] == 'U') {
                            column[i - 1] = generateMovement();
                            column[i] = 'X';
                            previousColumn[i - 1] = '-';
                        }
                        if (previousColumn[i] == 'R') {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == 'X') {
                            column[i] = '-';
                        }
                        if (column[i] == 0) {
                            column[i] = '-';
                        }
                    }
                } else {
                    if (previousColumn[i] != '-' || previousColumn[i - 1] != '-' || previousColumn[i + 1] != '-') {
                        if (previousColumn[i] == 'D') {
                            column[i + 1] = generateMovement();
                            column[i] = 'X';
                            previousColumn[i + 1] = '-';
                        }
                        if (previousColumn[i] == 'U') {
                            column[i - 1] = generateMovement();
                            column[i] = 'X';
                            previousColumn[i - 1] = '-';
                        }
                        if (previousColumn[i] == 'B') {
                            column[i - 1] = generateMovement();
                            column[i + 1] = generateMovement();
                            column[i] = 'X';
                            previousColumn[i - 1] = '-';
                            previousColumn[i + 1] = '-';
                        }
                        if (previousColumn[i] == 'R') {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == 'X') {
                            column[i] = '-';
                        }
                        if (column[i] == 0) {
                            column[i] = '-';
                        }
                    } else {
                        column[i] = '-';
                    }
                }
            }
        }
        return column;
    }
    
    public void generateLattice() {
        char[] column = generateColumn(null, 0);
        for (int j = 0; j < height; j++) {
            lattice[j][0] = column[j];
        }
        for (int i = 1; i < width; i++) {
            column = generateColumn(getColumn(lattice, i - 1), 0);
            for (int j = 0; j < height; j++) {
                lattice[j][i] = column[j];
            }
        }
    }
     
    private char generateMovement() {
        Random random = new Random();
        int movement = random.nextInt(100);
        if (movement < 30) {
            return 'R';
        }
        if (movement >= 30 && movement < 60) {
            return 'U';
        }
        if (movement >= 60 && movement < 90) {
            return 'D';
        }
        if (movement >= 90) {
            return 'B';
        }
        return '-';
    }
    
    public int[][] getBinaryLattice() {
    	int[][] returnLattice = new int[height][width];
    	for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (lattice[i][j] != '-') {
                    returnLattice[i][j] = 0;
                } else {
                    returnLattice[i][j] = 1;
                }
            }
        }
    	return returnLattice;
    }
    
    private char[] getColumn(char[][] lattice, int column) {
        char[] returnColumn = new char[height];
        for (int i = 0; i < height; i++) {
            returnColumn[i] = lattice[i][column];
        }
        return returnColumn;
    }
    
    public char[][] getLattice() {
    	return this.lattice;
    }
    
    public int[][][] getLocationLattice(int screenHeight, int screenWidth) {
    	int heightIntervals = screenHeight / height;
    	int widthIntervals = screenWidth / width;
    	int[][][] location = new int[height][width][2];
    	for (int i = 0; i < height; i++) {
    		for (int j = 0; j < width; j++) {
    			location[i][j][0] = i * heightIntervals;
    			location[i][j][1] = j * widthIntervals;
    		}
    	}
    	return location;
    }
    
    private void initLattice() {
    	lattice = new char[height][width];
    	for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                lattice[j][i] = '-';
            }
        }
    }
    
    public void printColumn(String[][] lattice, int column) {
        for (int i = 0; i < height; i++) {
            System.out.print(lattice[i][column] + " ");
        }
        System.out.print("\n");
    }
    
    public void printLattice(char[][] lattice) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(lattice[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
    
    public void printLattice(int[][] lattice) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(lattice[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
    
    public void printLattice(int[][][] lattice, int heightIntervals, int widthIntervals) {
        for (int i = 0; i < heightIntervals; i++) {
            for (int j = 0; j < widthIntervals; j++) {
                System.out.print("(" + lattice[i][j][0] + "," + lattice[i][j][1] + ") ");
            }
            System.out.print("\n");
        }
    }
    
    public void printLatticeBlank(char[][] lattice) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (lattice[i][j] != '-') {
                    System.out.print("  ");
                } else {
                    System.out.print(lattice[i][j] + " ");
                }
            }
            System.out.print("\n");
        }
    }
}