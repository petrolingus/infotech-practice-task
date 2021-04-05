package me.petrolingus.infotechpracticetask.infrastructure;

import javafx.collections.FXCollections;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextField;
import me.petrolingus.infotechpracticetask.domain.Harmonic;
import me.petrolingus.infotechpracticetask.domain.generator.DiscreteSignalGenerator;
import me.petrolingus.infotechpracticetask.domain.generator.HarmonicGenerator;
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

        double[] signalData = discreteSignalGenerator.generate();

        // TODO: 06.04.2021 Добавить генерацию шума

        XYChart.Series<Number, Number> signalSeries = arrayToSeries(signalData);
        signalChart.getData().clear();
        signalChart.getData().add(signalSeries);


        double[] autocorrelation = new double[256];
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
        XYChart.Series<Number, Number> autocorrelationSeries = arrayToSeries(autocorrelation);
        autocorrelationChart.getData().clear();
        autocorrelationChart.getData().add(autocorrelationSeries);

//        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
//        Complex[] complex = fastFourierTransformer.transform(autocorrelation, TransformType.FORWARD);

//        double[] spectrum = new double[complex.length];
//        for (int i = 0; i < spectrum.length; i++) {
//            spectrum[i] = complex[i].abs();
//        }
//        XYChart.Series<Number, Number> spectrumSeries = arrayToSeries(spectrum);
//        spectrumChart.getData().clear();
//        spectrumChart.getData().add(spectrumSeries);

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
            double ratio = singularValues[i] / maxSingularValue;
            if (ratio < 1e-3) {
                singularValues[i] = 0;
            }
        }

        RealMatrix U = decomposition.getU();
        RealMatrix S = MatrixUtils.createRealDiagonalMatrix(singularValues);
        RealMatrix V = decomposition.getV().transpose();

        RealMatrix A2 = U.multiply(S).multiply(V);

        double[] autocorrelation2 = new double[A2.getRowDimension()];
        for (int i = 0; i < autocorrelation2.length; i++) {
            autocorrelation2[i] = A2.getEntry(0, i);
        }
        XYChart.Series<Number, Number> autocorrelationSeries2 = arrayToSeries(autocorrelation2);
        autocorrelationChart.getData().add(autocorrelationSeries2);
    }

    private XYChart.Series<Number, Number> arrayToSeries(double[] data) {
        List<XYChart.Data<Number, Number>> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            list.add(new XYChart.Data<>(i, data[i]));
        }
        return new XYChart.Series<>(FXCollections.observableList(list));
    }
}
