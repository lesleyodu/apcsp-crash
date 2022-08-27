/*
 Extendable class to support simple command blocks
 Extend to support selection and iteration
 */

import java.util.ArrayList;
public class Command {
    private ArrayList<String> cblock;
    public Command() {
        cblock = new ArrayList<String>();
    }
    public void addCommand(String c) {
        cblock.add(c);
    }
    public ArrayList<String> getCommandBlock() {
        return cblock;
    }
    public String getCommandType() {
        return "simple";
    }
}