public abstract class Simulation {
    private static int countReplications = -1;
    private static int actualReplication = -1;
    private int pause;
    private int jumpRepl = 1;

    protected ISimData guiListener;
    private boolean isRunning = false;

    public void simulate(int replications) {
        isRunning = true;
        pause = (int)(0.3 * replications);
        if ((replications - pause) / 1000 != 0) {
            jumpRepl = (replications - pause) / 1000;
        } else {
            jumpRepl = 1;
        }
        countReplications = replications;
        beforeReplications();
        for (actualReplication = 1; actualReplication <= replications; actualReplication++) {
            if (!isRunning) {
                break;
            }
            beforeReplication();
            replication();
            afterReplication();
        }
        afterReplications();
        isRunning = false;
    }

    protected abstract void beforeReplications();
    protected abstract void beforeReplication();
    protected abstract void replication();
    protected abstract void afterReplication();
    protected abstract void afterReplications();

    public void addGuiListener(ISimData _guiListener) {
        guiListener = _guiListener;
    }

    public void stopSimulation() {
        isRunning = false;
    }

    public static int getCountReplications() {
        return countReplications;
    }

    public static int getActualReplication() {
        return actualReplication;
    }

    public int getPause() {
        return pause;
    }

    public int getJumpRepl() {
        return jumpRepl;
    }
}
