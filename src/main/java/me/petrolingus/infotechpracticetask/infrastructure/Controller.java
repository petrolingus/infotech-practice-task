package me.petrolingus.infotechpracticetask.infrastructure;

import javafx.collections.FXCollections;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import me.petrolingus.infotechpracticetask.domain.Harmonic;
import me.petrolingus.infotechpracticetask.domain.generator.DiscreteSignalGenerator;
import me.petrolingus.infotechpracticetask.domain.generator.HarmonicGenerator;
import me.petrolingus.infotechpracticetask.domain.generator.NoiseGenerator;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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


    public TextField noiseField;
    public TextField thresholdField;
    public TextField startField;


    public void initialize() {
        onGenerateButton();
    }

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

        int start = Integer.parseInt(startField.getText());

        HarmonicGenerator harmonicGenerator = new HarmonicGenerator(
                minAmplitude, maxAmplitude, minFrequency, maxFrequency, minPhase, maxPhase, harmonicsCount);

        Harmonic[] harmonics = harmonicGenerator.generate();

        DiscreteSignalGenerator discreteSignalGenerator = new DiscreteSignalGenerator(
                samplesCount,
                samplingFrequency,
                harmonics);

        double[] signalData = discreteSignalGenerator.generate();

        NoiseGenerator noiseGenerator = new NoiseGenerator(signalData);
        signalData = noiseGenerator.generate(Double.parseDouble(noiseField.getText()));

        XYChart.Series<Number, Number> signalSeries = arrayToSeries(signalData, 0);
        signalChart.getData().clear();
        signalChart.getData().add(signalSeries);

        double[] autocorrelation = new double[signalData.length];
        for (int m = 0; m < autocorrelation.length; m++) {
            double value = 0;
            for (int i = 0; i < autocorrelation.length; i++) {
                if (i + m >= signalData.length) {
                    break;
                }
                value += signalData[i] * signalData[i + m];
            }
            autocorrelation[m] = value;
        }
        XYChart.Series<Number, Number> autocorrelationSeries = arrayToSeries(autocorrelation, start);
        autocorrelationChart.getData().clear();
        autocorrelationChart.getData().add(autocorrelationSeries);

        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complex = fastFourierTransformer.transform(autocorrelation, TransformType.FORWARD);

        double[] spectrum = new double[complex.length];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = complex[i].abs();
        }
        XYChart.Series<Number, Number> spectrumSeries = arrayToSeries(spectrum, 0);
        spectrumChart.getData().clear();
        spectrumChart.getData().add(spectrumSeries);

        double[][] A1 = new double[autocorrelation.length][autocorrelation.length];
        for (int i = 0; i < autocorrelation.length; i++) {
            for (int j = 0; j < autocorrelation.length; j++) {
                int index = Math.abs(i - j);
                A1[i][j] = autocorrelation[index];
            }
        }

        SingularValueDecomposition decomposition = new SingularValueDecomposition(MatrixUtils.createRealMatrix(A1));

        double[] singularValues = decomposition.getSingularValues();
//        System.out.println(Arrays.toString(singularValues));

        // Находим максимальное сингулярное число
        double maxSingularValue = singularValues[0];
        for (double singularValue : singularValues) {
            if (maxSingularValue < singularValue) {
                maxSingularValue = singularValue;
            }
        }

        // Обнуляем сигмы
        for (int i = 0; i < singularValues.length; i++) {
            double ratio = singularValues[i] * Double.parseDouble(thresholdField.getText());
            if (ratio < maxSingularValue) {
                singularValues[i] = 0;
            }
        }

        System.out.println(Arrays.toString(singularValues));

        RealMatrix U = decomposition.getU();
        RealMatrix S = MatrixUtils.createRealDiagonalMatrix(singularValues);
        RealMatrix V = decomposition.getV().transpose();

        RealMatrix A2 = U.multiply(S).multiply(V);

        double[] autocorrelation2 = new double[A2.getRowDimension()];
        for (int i = 0; i < autocorrelation2.length; i++) {
            autocorrelation2[i] = A2.getEntry(0, i);
        }
        XYChart.Series<Number, Number> autocorrelationSeries2 = arrayToSeries(autocorrelation2, start);
        autocorrelationChart.getData().add(autocorrelationSeries2);

        Complex[] complex2 = fastFourierTransformer.transform(autocorrelation2, TransformType.FORWARD);

        double[] spectrum2 = new double[complex2.length];
        for (int i = 0; i < spectrum.length; i++) {
            spectrum2[i] = complex2[i].abs();
        }
        XYChart.Series<Number, Number> spectrumSeries2 = arrayToSeries(spectrum2, 0);
        spectrumChart.getData().add(spectrumSeries2);
    }

    private XYChart.Series<Number, Number> arrayToSeries(double[] data, int start) {
        List<XYChart.Data<Number, Number>> list = new ArrayList<>();
        for (int i = start; i < data.length; i++) {
            list.add(new XYChart.Data<>(i, data[i]));
        }
        return new XYChart.Series<>(FXCollections.observableList(list));
    }
}
