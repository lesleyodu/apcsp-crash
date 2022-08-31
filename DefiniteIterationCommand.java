import java.util.ArrayList;
public class DefiniteIterationCommand extends IterationCommand {
    private int limit;
    private int reps;
    
    public DefiniteIterationCommand(int l) {
        super();
        limit = l;
        reps = 1;
    }
    public String getIterationType() {
        return "definite";
    }
    public ArrayList<String> getCommandBlock() {
        ArrayList<String> cmds = new ArrayList<String>();
        if (reps <= limit) {
            cmds = super.getCommandBlock();
        }
        reps++;
        return cmds;
    }
    public boolean hasEnded() {
        return reps > limit;
    }
}