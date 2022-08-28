import java.awt.Color;
import java.awt.Font;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

import lib.StdDraw;

public class WillItCrash {

   public static final double WIDTH = 1.0;
    
   public static final int MOVE_FORWARD = (int) 'f';
   public static final int MOVE_BACKWARD = (int) 'b';
   public static final int MOVE_LEFT = (int) 'l';
   public static final int MOVE_RIGHT = (int) 'r';
    
   public static final char EMPTY_CELL = '_';
   public static final char BLOCKED_CELL = 'x';
    
   public static final char CURSOR_LEFT = '<';
   public static final char CURSOR_RIGHT = '>';
   public static final char CURSOR_UP = '^';
   public static final char CURSOR_DOWN = 'v';
   public static final char CURSOR_END = '#';

   public static void main(String[] args) throws Exception {
    
      String inputName = (args.length > 0) ? args[0] : "u1";
      int endCell = -1;
    
      Scanner gridFile = new Scanner(new File("input-files/grid-"+ inputName + ".txt"));
      ArrayList<String> gridParse = new ArrayList<String>();
      while (gridFile.hasNextLine()) {
         gridParse.add(gridFile.nextLine());
      }
      char[][] grid = new char[gridParse.size()][gridParse.get(0).length()];
      for (int i = 0; i < gridParse.size(); i++) {
         grid[i] = gridParse.get(i).trim().toCharArray();
         if (gridParse.get(i).contains("*")) {
            int j = gridParse.get(i).indexOf("*");
            endCell = i * 10 + j;
            grid[i][j] = '_';
         }
      }
      
      Scanner commandFile = new Scanner(new File("input-files/commands-"+ inputName + ".txt"));
      ArrayList<String> commandFileParse = new ArrayList<String>();
      while (commandFile.hasNextLine()) {
          commandFileParse.add(commandFile.nextLine());
      }
      ArrayList<Command> commands = parseCommands(commandFileParse);
              
      initializeGrid(grid, endCell);

      makeCommands(commands, grid, endCell);
        
   }
   
   public static void makeCommands(ArrayList<Command> commands, char[][] grid, int endCell){
   
     for (int i = 0; i < commands.size() && !inEndState(grid); i++) {
         ArrayList<String> myCommands = commands.get(i).getCommandBlock();
         
         if (commands.get(i).getCommandType().equals("selection")) {
             myCommands = evaluateSelectionAndGetBlock(grid, commands.get(i));
         }
         
         if (commandBlockHasIf(myCommands)) {
             ArrayList<Command> subCommands = parseCommands(myCommands);
             makeCommands(subCommands, grid, endCell);
         }
         else {
         
             boolean result = true;
             for (int j = 0; j < myCommands.size() && result; j++) {
                 result = makeSimpleCommand(grid, endCell, myCommands.get(j));
             }
         }
      }
   }
   
   public static ArrayList<String> evaluateSelectionAndGetBlock(char[][] grid, Command cmd) {
            ArrayList<String> myCommands = new ArrayList<String>();
            //SelectionCommand selCast = (SelectionCommand) (commands.get(i));
            SelectionCommand selCast = (SelectionCommand) (cmd);
            ArrayList<String> selComs = selCast.getSelectionBlock();
            boolean selFound = false;
            for (int j = 0; j < selComs.size() && !selFound; j++) {
               String selCom = selComs.get(j);
               if (selCom.startsWith("ELSE")) {
                  selCast.setIndex(j);
                  myCommands = selCast.getCommandBlock();
                  selFound = true;
               }
               else {
                  String selDir = selCom.replaceAll("[^a-z]", "");
                  int selDirNum = 0;
                  if (selDir.equals("forward")) {
                     selDirNum = MOVE_FORWARD;
                  }
                  else if (selDir.equals("left")) {
                     selDirNum = MOVE_LEFT;
                  }
                  else if (selDir.equals("right")) {
                     selDirNum = MOVE_RIGHT;
                  }
                  else {
                     selDirNum = MOVE_BACKWARD;
                  }
                  boolean selEval = canMove(grid, selDirNum);
                  if (selEval) {
                     selCast.setIndex(j);
                     myCommands = selCast.getCommandBlock();
                     selFound = true;
                  }
               }
            }
            if (!selFound) {
               myCommands = new ArrayList<String>(); // false if no else
            }
         return myCommands;
   }
   
   public static boolean makeSimpleCommand(char[][] grid, int endCell, String myCommand) {
            boolean crash = false;
            boolean succeed = false;
            int curs = getCursorLocation(grid); 
            int cursor_i = curs / 10;
            int cursor_j = curs % 10;
            StdDraw.setPenColor(StdDraw.WHITE);
            double x = grid.length * WIDTH/2.0;
            double y = (grid.length + 0.5)* WIDTH;
            StdDraw.filledRectangle(x, y, (grid.length * WIDTH)/2.0, WIDTH * 0.5);
            StdDraw.setPenColor(StdDraw.BLACK);
            String commandText = myCommand;
            if (commandText.endsWith("()")){
               commandText = commandText.substring(0, commandText.length() - 2);
               commandText = commandText.trim();
            }
            StdDraw.text(x, y, commandText);
            StdDraw.show(1000);
            if (myCommand.startsWith("ROTATE_LEFT")) {
               grid[cursor_i][cursor_j] = rotateLeft(grid[cursor_i][cursor_j]);
            }
            else if (myCommand.startsWith("ROTATE_RIGHT")) {
               grid[cursor_i][cursor_j] = rotateRight(grid[cursor_i][cursor_j]);
            }
            else if (myCommand.startsWith("MOVE_FORWARD")) {
               if (canMove(grid, MOVE_FORWARD)) {
                  if (grid[cursor_i][cursor_j] == CURSOR_RIGHT) {
                     grid[cursor_i][cursor_j] = EMPTY_CELL;
                     grid[cursor_i][cursor_j + 1] = CURSOR_RIGHT;
                  }
                  else if (grid[cursor_i][cursor_j] == CURSOR_LEFT) {
                     grid[cursor_i][cursor_j] = EMPTY_CELL;
                     grid[cursor_i][cursor_j - 1] = CURSOR_LEFT;
                  }
                  else if (grid[cursor_i][cursor_j] == CURSOR_DOWN) {
                     grid[cursor_i][cursor_j] = EMPTY_CELL;
                     grid[cursor_i + 1][cursor_j] = CURSOR_DOWN;
                  }
                  else {
                     grid[cursor_i][cursor_j] = EMPTY_CELL;
                     grid[cursor_i - 1][cursor_j] = CURSOR_UP;
                  }
                   
                  if(getCursorLocation(grid) == endCell) {
                     succeed = true;
                  } 
               }
               else {
                  crash = true;
               }
            
            }
            drawGrid(grid, endCell);
            StdDraw.setPenColor(StdDraw.WHITE);
            StdDraw.filledRectangle(x, y, (grid.length * WIDTH)/2.0, WIDTH * 0.5);
            if (crash) {
               StdDraw.setPenColor(StdDraw.RED);
               StdDraw.text(x, y, "CRASH!");
               StdDraw.show(1000);
               curs = getCursorLocation(grid); 
               cursor_i = curs / 10;
               cursor_j = curs % 10;
               grid[cursor_i][cursor_j] = CURSOR_END;
               return false;
            }
            if (succeed) {
               StdDraw.setPenColor(StdDraw.BOOK_BLUE );
               StdDraw.text(x, y, "GOAL!!!");
               StdDraw.show(1000);
               curs = getCursorLocation(grid); 
               cursor_i = curs / 10;
               cursor_j = curs % 10;
               grid[cursor_i][cursor_j] = CURSOR_END;
               return false; 
            }
            StdDraw.show(1000);
            return true;
   }
   
   public static boolean commandBlockHasIf(ArrayList<String> commandList) {
       for (int i = 0; i < commandList.size(); i++) {
           String cmd = commandList.get(i).trim();
           if (cmd.startsWith("IF")) {
               return true;
           }
       }
       return false;
   }
   
   public static ArrayList<Command> parseCommands(ArrayList<String> commandList) {
      //tiny parser
      //to do: nested if, iteration
      ArrayList<Command> commands = new ArrayList<Command>();
      commands.add(new Command());
      for (int i = 0; i < commandList.size(); i++) {
         String cmd = commandList.get(i).trim();
            //need to re-parse nested if
         if (cmd.startsWith("IF")) {
            SelectionCommand scom = new SelectionCommand();
            scom.addSelectionBlock(cmd);
            i++;
            cmd = commandList.get(i).trim();
            int curly = 0;
            boolean stop = false;
            while (!stop) { 
               if (cmd.equals("{")) {
                   curly++;
                  if (curly > 1) {
                      scom.addCommand(cmd);
                  }
               }
               else {
                   scom.addCommand(cmd);
               }
               i++;
               cmd = commandList.get(i).trim();
               if (cmd.equals("}")) {
                   curly--;
                   if (curly == 0) {
                       stop = true;
                   }
               }
            }
            commands.add(scom);
            commands.add(new Command());
         }
         else if (cmd.startsWith("ELSE")) {
            Command theIf = commands.get(commands.size() - 2);
            SelectionCommand scom = (SelectionCommand) (theIf);
            scom.addSelectionBlock(cmd);
            i++;
            cmd = commandList.get(i).trim();
            int curly = 0;
            boolean stop = false;
            while (!stop) { 
               if (cmd.equals("{")) {
                   curly++;
                  if (curly > 1) {
                      scom.addCommand(cmd);
                  }
               }
               else {
                   scom.addCommand(cmd);
               }
               i++;
               cmd = commandList.get(i).trim();
               //example 29 is malformed - each curly brace should be on its own line
               if (cmd.equals("}")) {
                   curly--;
                   if (curly == 0) {
                       stop = true;
                   }
               }
            }
         
         }
         else {
            commands.get(commands.size() - 1).addCommand(cmd);
         }
      }
      return commands;
   }
   
   public static void initializeGrid(char[][] grid, int endCell) {
      StdDraw.setXscale(0, grid.length * WIDTH);
      StdDraw.setYscale(0, (grid.length + 1) * WIDTH);
      Font font = new Font("Arial", Font.BOLD, 60);
      StdDraw.setFont(font);
      drawGrid(grid, endCell);
      StdDraw.show(2000);
   }
   public static void drawGrid(char[][] grid, int endCell) {
      for (int i = 0; i < grid.length; i++) {
         for (int j = 0; j < grid[i].length; j++) {
            if (endCell != -1 && endCell / 10 == i && endCell % 10 == j) {
               StdDraw.setPenColor(StdDraw.BOOK_BLUE);
            }
            else {
               StdDraw.setPenColor(getGridColor(grid[i][j]));
            }
            double x = j + WIDTH / 2;
            double y = grid.length - i - WIDTH / 2;
            StdDraw.filledRectangle(x, y, WIDTH / 2, WIDTH / 2);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.rectangle(x, y, WIDTH / 2, WIDTH / 2);
            if (isCursor(grid[i][j])) {
               drawCursor(grid[i][j], x, y);
                    //StdDraw.text(x, y, "" + grid[i][j]);
            }
         }
      }
   
   }
   public static Color getGridColor(char c) {
      if (c == BLOCKED_CELL) 
         return StdDraw.GRAY;
      else          
         return StdDraw.WHITE; //cursor
   }
   public static boolean isCursor(char c) {
      return c == CURSOR_RIGHT || c == CURSOR_LEFT || c == CURSOR_UP || c == CURSOR_DOWN;
   }
   public static char rotateLeft(char c) {
      char[] rot = {CURSOR_RIGHT, CURSOR_UP, CURSOR_LEFT, CURSOR_DOWN};
      for (int i = 0; i < rot.length; i++) {
         if (c == rot[i]) {
            return rot[(i + 1) % rot.length];
         }
      }
      return c;
   }
   public static char rotateRight(char c) {
      char[] rot = {CURSOR_RIGHT, CURSOR_DOWN, CURSOR_LEFT, CURSOR_UP};
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
   
   public static boolean inEndState (char[][] grid) {
      for (int i = 0; i < grid.length; i++) {
         for (int j = 0; j < grid[i].length; j++) {
            if (grid[i][j] == CURSOR_END) {
               return true;
            }
         }
      }
      return false;
   }
    
     /**
    * Displays a triangle based on the character passed in the first arg at the location set by the second and third args. 
    *
    * @param  c a character representing the cursor, one of '^', '<', '<', 'v'
    * @param  x the 'x' coordinate where the cursor should appear on the screen
    * @param  y the 'y' coordinate where the cursor should appear on the screen
    */
   public static void drawCursor(char c, double x, double y) {
        // validate (I know this should be an interface or whatever but I am lazy and nothing is encapsulated yet)
      if (!isCursor(c)) {
         throw new Error("First arg must be one of the following characters: 'v', '^', '>', or '<'");
      }
        
      final double triangleScale = 0.3 * WIDTH; // Set size of triangle relative to WIDTH
   
        // default vertices coords for upward-facing triangle, assuming c = '^'
      double[] verticesX = {x, x+WIDTH*triangleScale, x-WIDTH*triangleScale};
      double[] verticesY = {y+WIDTH*triangleScale, y-WIDTH*triangleScale, y-WIDTH*triangleScale};
   
        // adjust vertex coords based on true value of 'c'
      if (c == CURSOR_RIGHT) {
         verticesX[0] = x-WIDTH*triangleScale;
         verticesY[1] = y;
      } else if(c == CURSOR_LEFT) {
         verticesX[0] = x+WIDTH*triangleScale;
         verticesY[2] = y;
      } else if(c == CURSOR_DOWN) {
         verticesY[0] = y-WIDTH*triangleScale;
         verticesY[1] = y+WIDTH*triangleScale;
         verticesY[2] = y+WIDTH*triangleScale;
      }
        // display the cursor
      StdDraw.filledPolygon(verticesX, verticesY);
   }
    
   public static boolean canMove(char[][] grid, int direction) {
      int curs = getCursorLocation(grid); 
      int cursor_i = curs / 10;
      int cursor_j = curs % 10;
      if (grid[cursor_i][cursor_j] == CURSOR_RIGHT && direction == MOVE_FORWARD ||
            grid[cursor_i][cursor_j] == CURSOR_LEFT && direction == MOVE_BACKWARD ||
            grid[cursor_i][cursor_j] == CURSOR_UP && direction == MOVE_RIGHT ||
            grid[cursor_i][cursor_j] == CURSOR_DOWN && direction == MOVE_LEFT) {
         return cursor_j < grid[cursor_i].length - 1 && grid[cursor_i][cursor_j + 1] == EMPTY_CELL;
      }
      else if (grid[cursor_i][cursor_j] == CURSOR_LEFT && direction == MOVE_FORWARD ||
                 grid[cursor_i][cursor_j] == CURSOR_RIGHT && direction == MOVE_BACKWARD ||
                 grid[cursor_i][cursor_j] == CURSOR_UP && direction == MOVE_LEFT ||
                 grid[cursor_i][cursor_j] == CURSOR_DOWN && direction == MOVE_RIGHT) {
         return cursor_j > 0 && grid[cursor_i][cursor_j - 1] == EMPTY_CELL;
      }
      else if (grid[cursor_i][cursor_j] == CURSOR_DOWN && direction == MOVE_FORWARD ||
                 grid[cursor_i][cursor_j] == CURSOR_UP && direction == MOVE_BACKWARD ||
                 grid[cursor_i][cursor_j] == CURSOR_LEFT && direction == MOVE_LEFT ||
                 grid[cursor_i][cursor_j] == CURSOR_RIGHT && direction == MOVE_RIGHT) {
         return cursor_i < grid.length - 1 && grid[cursor_i + 1][cursor_j] == EMPTY_CELL;
      }
      else {
         return cursor_i > 0 && grid[cursor_i - 1][cursor_j] == EMPTY_CELL;
      }
   }
}