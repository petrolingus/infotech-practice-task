package me.petrolingus.infotechpracticetask.infrastructure;

import javafx.collections.FXCollections;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import me.petrolingus.infotechpracticetask.domain.Harmonic;
import me.petrolingus.infotechpracticetask.domain.generator.DiscreteSignalGenerator;
import me.petrolingus.infotechpracticetask.domain.generator.HarmonicGenerator;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller {

    public AreaChart<Number, Number> signalChart;

    public AreaChart<Number, Number> autocorrelationChart;

    public AreaChart<Number, Number> spectrumChart;


    public TextField minAmplitudeField;

    public TextField maxAmplitudeField;

    public TextField minFrequencyField;

    public TextField maxFrequencyField;

    public TextField minPhaseField;

    public TextField maxPhaseField;


    public TextField samplesCountField;

    public TextField harmonicsCountField;

    public TextField samplingFrequencyField;


    public void onGenerateButton() {

        int samplesCount = Integer.parseInt(samplesCountField.getText());
        int harmonicsCount = Integer.parseInt(harmonicsCountField.getText());
        double samplingFrequency = Double.parseDouble(samplingFrequencyField.getText());

        double minAmplitude = Double.parseDouble(minAmplitudeField.getText());
        double maxAmplitude = Double.parseDouble(maxAmplitudeField.getText());
        double minFrequency = Double.parseDouble(minFrequencyField.getText());
        double maxFrequency = Double.parseDouble(maxFrequencyField.getText());
        double minPhase = Double.parseDouble(minPhaseField.getText());
        double maxPhase = Double.parseDouble(maxPhaseField.getText());

        HarmonicGenerator harmonicGenerator = new HarmonicGenerator(
                minAmplitude, maxAmplitude, minFrequency, maxFrequency, minPhase, maxPhase, harmonicsCount);

        Harmonic[] harmonics = harmonicGenerator.generate();

        DiscreteSignalGenerator discreteSignalGenerator = new DiscreteSignalGenerator(
                samplesCount,
                samplingFrequency,
                harmonics);

        double[] signalArray = discreteSignalGenerator.generate();

//        NoiseGenerator noiseGenerator = new NoiseGenerator(signalArray);

//        double[] signalWithNoiseArray = noiseGenerator.generate();

        XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>(
                FXCollections.observableList(IntStream.range(0, signalArray.length)
                        .mapToObj(i -> new XYChart.Data<Number, Number>(i, signalArray[i]))
                        .collect(Collectors.toList()))
        );

        signalChart.getData().clear();
        signalChart.getData().add(signalSeries);
    }
}
