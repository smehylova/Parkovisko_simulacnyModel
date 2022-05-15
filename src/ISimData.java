import java.util.ArrayList;

public interface ISimData {
    public void sendGraphValue(int countReplications, int value);
    public void sendHistogramData(ArrayList<Integer> values);
}
