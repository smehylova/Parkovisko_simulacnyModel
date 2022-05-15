import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainForm extends JFrame implements ISimData {

    private JPanel GraphPanel;
    private JPanel MainPanel;
    private JLabel LTitle;
    private JLabel LCountPlaces;
    private JTextField tfCountPlaces;
    private JLabel lReplications;
    private JButton btnStart;
    private JButton btnStop;
    private JSpinner sCountPlaces;
    private JSpinner sCountReplications;
    private JLabel lResult;
    private JLabel lTextResult;
    private JLabel lStrategy;
    private JComboBox cbStrategy;
    private JTabbedPane tabbedPane1;
    private JPanel HistogramPanel;

    //atributy pre simulaciu
    private static Parking parking;
    private static int strategy;
    private static ISimData self;

    //atributy pre graf
    private static JFreeChart chart;
    private static final XYSeriesCollection dataset = new XYSeriesCollection();
    private static final XYSeries series = new XYSeries("Strategy");

    //adtributy pre histogram
    private static JFreeChart histogram;
    private static DefaultCategoryDataset datasetHistogram;

    public MainForm() {
        self = this;
        drawGraph();
        drawHistogram();
        setContentPane(MainPanel);
        setTitle("Parking");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        btnStop.setEnabled(false);

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Thread thread = new Thread(){
                    public void run(){
                        //zistenie zaciatku hladania miesta podla strategie
                        switch (cbStrategy.getSelectedIndex()) {
                            case 0:
                                strategy = 0;
                                break;
                            case 1:
                                if ((int)sCountPlaces.getValue() / 2.0 > (double)((int)sCountPlaces.getValue() / 2)) {
                                    strategy = ((int)sCountPlaces.getValue() / 2) + 1;
                                } else {
                                    strategy = (int)sCountPlaces.getValue() / 2;
                                }
                                break;
                            case 2:
                                if ((int)sCountPlaces.getValue() * 2 / 3.0 > (double)((int)sCountPlaces.getValue() * 2 / 3)) {
                                    strategy = ((int)sCountPlaces.getValue() * 2 / 3) + 1;
                                } else {
                                    strategy = (int)sCountPlaces.getValue() * 2 / 3;
                                }
                                break;
                        }
                        btnStart.setEnabled(false);
                        btnStop.setEnabled(true);

                        //vymazanie dat z grafu a histogramu
                        series.clear();
                        datasetHistogram.clear();
                        lTextResult.setText("...");

                        //spustenie simulacie
                        parking = new Parking((int)sCountPlaces.getValue(), strategy);
                        parking.addGuiListener(self);
                        parking.simulate((int)sCountReplications.getValue());

                        btnStart.setEnabled(true);
                        btnStop.setEnabled(false);
                    }
                };

                thread.start();
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parking.stopSimulation();
            }
        });
    }

    @Override
    public void sendGraphValue(int countReplications, int value) {
        series.add(countReplications, (double)value / countReplications);
        if (series.getMinY() != series.getMaxY()) {
            chart.getXYPlot().getRangeAxis().setRange(series.getMinY(), series.getMaxY());
        }
        lTextResult.setText((double)value / countReplications + "");
    }

    @Override
    public void sendHistogramData(ArrayList<Integer> values) {
        for (int i = 0; i < values.size(); i++) {
            datasetHistogram.setValue(values.get(i), "Strategy", String.valueOf(i + 1));
        }
    }

    public void drawGraph() {
        dataset.addSeries(series);
        chart = ChartFactory.createXYLineChart(
                "Parking",
                "Replications",
                "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );
        ChartPanel chartPanel = new ChartPanel(chart);
        GraphPanel.add(chartPanel);
        GraphPanel.validate();
        chartPanel.setVisible(true);
    }

    public void drawHistogram() {
        datasetHistogram = new DefaultCategoryDataset();
        //datasetHistogram.
        histogram = ChartFactory.createBarChart(
                "Parking",
                "Place",
                "Frequency",
                datasetHistogram,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        ChartPanel histPanel = new ChartPanel(histogram);
        HistogramPanel.add(histPanel);
        HistogramPanel.validate();
        histPanel.setVisible(true);
    }
}
