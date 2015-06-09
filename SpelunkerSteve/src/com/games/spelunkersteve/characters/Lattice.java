/**
* @Author: Christopher Washington
* @brief: Prototype for lattice path class.
*/

import java.util.Random;

public class HelloWorld {
    static final int LATTICE_HEIGHT = 10;
    static final int LATTICE_WIDTH = 100;
    static final int RIGHT = 0;
    static final int UP = 1;
    static final int DOWN  = 2;
    static final int CLOSE = 3;
    static String[][] lattice = new String[LATTICE_HEIGHT][LATTICE_WIDTH];

    public static void main(String []args){
        for (int i = 0; i < LATTICE_WIDTH; i++) {
            for (int j = 0; j < LATTICE_HEIGHT; j++) {
                lattice[j][i] = "-";
            }
        }
        generateLattice();
        printLatticeBlank(lattice);
    }
     
    private static void printLattice(String[][] lattice) {
        for (int i = 0; i < LATTICE_HEIGHT; i++) {
            for (int j = 0; j < LATTICE_WIDTH; j++) {
                System.out.print(lattice[i][j] + " ");
            }
            System.out.print("\n");
        }
    }
    
    private static void printLatticeBlank(String[][] lattice) {
        for (int i = 0; i < LATTICE_HEIGHT; i++) {
            for (int j = 0; j < LATTICE_WIDTH; j++) {
                if (lattice[i][j] != "-") {
                    System.out.print("  ");
                } else {
                    System.out.print(lattice[i][j] + " ");
                }
            }
            System.out.print("\n");
        }
    }
    
    private static void printColumn(String[][] lattice, int column) {
        for (int i = 0; i < LATTICE_HEIGHT; i++) {
            System.out.print(lattice[i][column] + " ");
        }
        System.out.print("\n");
    }
    
    private static String[] getColumn(String[][] lattice, int column) {
        String[] returnColumn = new String[LATTICE_HEIGHT];
        for (int i = 0; i < LATTICE_HEIGHT; i++) {
            returnColumn[i] = lattice[i][column];
        }
        return returnColumn;
    }
    
    private static String[] generateColumn(String[] previousColumn, int difficulty) {
        String[] column = new String[LATTICE_HEIGHT];
        column[0] = "-";
        column[LATTICE_HEIGHT - 1] = "-";
        if (previousColumn == null) {
            for (int i = 1; i < LATTICE_HEIGHT - 1; i++) {
                column[i] = "-";
                if (i == 4) {
                    column[i] = "U";
                }
                if (i == 5) {
                    column[i] = "R";
                }
                if (i == 6) {
                    column[i] = "D";
                }
            }
        } else {
            for (int i = 0; i < LATTICE_HEIGHT; i++) {
                if (i == 0 || i == LATTICE_HEIGHT - 1) {
                    if (i == 0) {
                        if (previousColumn[i] == "D") {
                            column[i + 1] = generateMovement();
                            column[i] = "X";
                            previousColumn[i + 1] = "-";
                        }
                        if (previousColumn[i] == "U") {
                            column[i] = "D";
                        }
                        if (previousColumn[i] == "R") {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == "X") {
                            column[i] = "-";
                        }
                        if (column[i] == null) {
                            column[i] = "-";
                        }
                    }
                    if (i == LATTICE_HEIGHT - 1) {
                        if (previousColumn[i] == "D") {
                            column[i] = "U";
                        }
                        if (previousColumn[i] == "U") {
                            column[i - 1] = generateMovement();
                            column[i] = "X";
                            previousColumn[i - 1] = "-";
                        }
                        if (previousColumn[i] == "R") {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == "X") {
                            column[i] = "-";
                        }
                        if (column[i] == null) {
                            column[i] = "-";
                        }
                    }
                } else {
                    if (previousColumn[i] != "-" || previousColumn[i - 1] != "-" || previousColumn[i + 1] != "-") {
                        if (previousColumn[i] == "D") {
                            column[i + 1] = generateMovement();
                            column[i] = "X";
                            previousColumn[i + 1] = "-";
                        }
                        if (previousColumn[i] == "U") {
                            column[i - 1] = generateMovement();
                            column[i] = "X";
                            previousColumn[i - 1] = "-";
                        }
                        if (previousColumn[i] == "R") {
                            column[i] = generateMovement();
                        }
                        if (previousColumn[i] == "X") {
                            column[i] = "-";
                        }
                        if (column[i] == null) {
                            column[i] = "-";
                        }
                    } else {
                        column[i] = "-";
                    }
                }
            }
        }
        return column;
    }
    
    private static void generateLattice() {
        String[] column = generateColumn(null, 0);
        for (int j = 0; j < LATTICE_HEIGHT; j++) {
            lattice[j][0] = column[j];
        }
        for (int i = 1; i < LATTICE_WIDTH; i++) {
            column = generateColumn(getColumn(lattice, i - 1), 0);
            for (int j = 0; j < LATTICE_HEIGHT; j++) {
                lattice[j][i] = column[j];
            }
        }
    }
    
    private static String generateMovement() {
        Random random = new Random();
        int movement = random.nextInt(3);
        if (movement == RIGHT) {
            return "R";
        }
        if (movement == UP) {
            return "U";
        }
        if (movement == DOWN) {
            return "D";
        }
        return "-";
    }
}