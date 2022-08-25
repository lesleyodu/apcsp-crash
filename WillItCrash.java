import java.awt.Color;
import java.awt.Font;

public class WillItCrash {

    public static final double WIDTH = 1.0;

    public static void main(String[] args) {
        
        char[][] grid = {{'x', '_', '_', '_', 'x'},
                         {'_', 'x', '_', '_', '_'},
                         {'_', '_', '>', '_', '_'},
                         {'_', '_', 'x', '_', '_'},
                         {'_', 'x', '_', 'x', '_'}};
        String[] commands = {"MOVE_FORWARD", "ROTATE_LEFT", "MOVE_FORWARD", "ROTATE_LEFT",
                             "MOVE_FORWARD", "ROTATE_LEFT", "MOVE_FORWARD", "ROTATE_LEFT",
                             "MOVE_FORWARD", "ROTATE_LEFT", "MOVE_FORWARD",
                             "ROTATE_RIGHT", "MOVE_FORWARD", "ROTATE_RIGHT", "MOVE_FORWARD", 
                             "ROTATE_RIGHT", "MOVE_FORWARD", "ROTATE_RIGHT", "MOVE_FORWARD"};
        initializeGrid(grid);
        boolean crash = false;
        for (int i = 0; i < commands.length; i++) {
            crash = false;
            int curs = getCursorLocation(grid); 
            int cursor_i = curs / 10;
            int cursor_j = curs % 10;
            StdDraw.setPenColor(StdDraw.WHITE);
            double x = grid.length * WIDTH/2.0;
            double y = (grid.length + 0.5)* WIDTH;
            StdDraw.filledRectangle(x, y, (grid.length * WIDTH)/2.0, WIDTH * 0.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(x, y, commands[i]);
            StdDraw.show(1000);
            if (commands[i].equals("ROTATE_LEFT")) {
                grid[cursor_i][cursor_j] = rotateLeft(grid[cursor_i][cursor_j]);
            }
            else if (commands[i].equals("ROTATE_RIGHT")) {
                grid[cursor_i][cursor_j] = rotateRight(grid[cursor_i][cursor_j]);
            }
            else if (commands[i].equals("MOVE_FORWARD")) {
                if (grid[cursor_i][cursor_j] == '>') {
                    if (cursor_j < grid[cursor_i].length - 1) {
                        if (grid[cursor_i][cursor_j + 1] == '_') {
                            grid[cursor_i][cursor_j] = '_';
                            grid[cursor_i][cursor_j + 1] = '>';
                        }
                        else {
                            crash = true;
                        }
                    }
                    else {
                        crash = true;
                    }
                }
                else if (grid[cursor_i][cursor_j] == '<') {
                    if (cursor_j > 0) {
                        if (grid[cursor_i][cursor_j - 1] == '_') {
                            grid[cursor_i][cursor_j] = '_';
                            grid[cursor_i][cursor_j - 1] = '<';
                        }
                        else {
                            crash = true;
                        }
                    }
                    else {
                        crash = true;
                    }
                }
                else if (grid[cursor_i][cursor_j] == 'v') {
                    if (cursor_i < grid.length - 1) {
                        if (grid[cursor_i + 1][cursor_j] == '_') {
                            grid[cursor_i][cursor_j] = '_';
                            grid[cursor_i + 1][cursor_j] = 'v';
                        }
                        else {
                            crash = true;
                        }
                    }
                    else {
                        crash = true;
                    }
                }
                else {
                    if (cursor_i > 0) {
                        if (grid[cursor_i - 1][cursor_j] == '_') {
                            grid[cursor_i][cursor_j] = '_';
                            grid[cursor_i - 1][cursor_j] = '^';
                        }
                        else {
                            crash = true;
                        }
                    }
                    else {
                        crash = true;
                    }
                }

            }
            drawGrid(grid);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle(x, y, (grid.length * WIDTH)/2.0, WIDTH * 0.5);
            if (crash) {
                StdDraw.setPenColor(StdDraw.RED);
                StdDraw.text(x, y, "CRASH!");
                StdDraw.show(1000);
                break;
            }
            StdDraw.show(1000);
        }
        
    }
    public static void initializeGrid(char[][] grid) {
        StdDraw.setXscale(0, grid.length * WIDTH);
        StdDraw.setYscale(0, (grid.length + 1) * WIDTH);
        Font font = new Font("Arial", Font.BOLD, 60);
        StdDraw.setFont(font);
        drawGrid(grid);
        StdDraw.show(2000);
    }
    public static void drawGrid(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                StdDraw.setPenColor(getGridColor(grid[i][j]));
                double x = j + WIDTH / 2;
                double y = grid.length - i - WIDTH / 2;
                StdDraw.filledRectangle(x, y, WIDTH / 2, WIDTH / 2);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.rectangle(x, y, WIDTH / 2, WIDTH / 2);
                if (isCursor(grid[i][j])) {
                    StdDraw.text(x, y, "" + grid[i][j]);
                }
            }
        }

    }
    public static Color getGridColor(char c) {
        if (c == 'x') return StdDraw.GRAY;
        else          return StdDraw.WHITE; //cursor
    }
    public static boolean isCursor(char c) {
        return c == '>' || c == '<' || c == '^' || c == 'v';
    }
    public static char rotateLeft(char c) {
        char[] rot = {'>', '^', '<', 'v'};
        for (int i = 0; i < rot.length; i++) {
            if (c == rot[i]) {
                return rot[(i + 1) % rot.length];
            }
        }
        return c;
    }
    public static char rotateRight(char c) {
        char[] rot = {'>', 'v', '<', '^'};
        for (int i = 0; i < rot.length; i++) {
            if (c == rot[i]) {
                return rot[(i + 1) % rot.length];
            }
        }
        return c;
    }
    public static int getCursorLocation(char[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (isCursor(grid[i][j])) {
                    return i * 10 + j;
                }
            }
        }
        return -1;
    }
}