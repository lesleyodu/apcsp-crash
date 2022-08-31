public abstract class IterationCommand extends Command {
    public String getCommandType() {
        return "iteration";
    }
    public abstract String getIterationType();
    public abstract boolean hasEnded();
}