import java.util.ArrayList;
public class SelectionCommand extends Command {
    private ArrayList<Command> sblock;
    private int index;
    public SelectionCommand() {
        super();
        sblock = new ArrayList<Command>();
        index = -1;
    }
    public void addCommand(String command) {
        sblock.get(index).addCommand(command);
    }
    public void setIndex(int i) {
        index = i;
    }
    public ArrayList<String> getCommandBlock() {
       if (index >= 0 && index < sblock.size()) {
           return sblock.get(index).getCommandBlock();
       }
       else {
           return new ArrayList<String>(); //if without else
       }
    }
    public String getCommandType() {
        return "selection";
    }
    public void addSelectionBlock(String c) {
        super.addCommand(c);
        sblock.add(new Command());
        index++;
    }
    public ArrayList<String> getSelectionBlock() {
        //I coded this thinking there could be else-if's, but this is always size 1 or 2
        return super.getCommandBlock();
    }
    
}