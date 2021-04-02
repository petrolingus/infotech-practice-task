package me.petrolingus.infotechpracticetask;

import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Controller {

    public AreaChart<Number, Number> signalChart;

    public AreaChart<Number, Number> autocorrelationChart;

    public AreaChart<Number, Number> spectrumChart;

    public void initialize() {

        int samplesCount = 1024;

        double[] signalArray = new double[samplesCount];

        XYChart.Series<Number, Number> signalSeries = new XYChart.Series<>();
        for (int i = 0; i < samplesCount; i++) {
            double d = 1.0 / 256.0;
            double value = Math.sin(2 * Math.PI * d * i);
            signalArray[i] = value;
            signalSeries.getData().add(new XYChart.Data<>(i, value));
        }

        signalChart.getData().add(signalSeries);

        double[] autocorrelation = new double[samplesCount];

        for (int m = 0; m < autocorrelation.length; m++) {
            double value = 0;
            for (int i = 0; i < samplesCount; i++) {
                if (i + m >= signalArray.length) {
                    break;
                }
                value += signalArray[i] * signalArray[i + m];
            }
            autocorrelation[m] = value;
        }

        XYChart.Series<Number, Number> autocorrelationSeries = new XYChart.Series<>();
        for (int i = 0; i < autocorrelation.length; i++) {
            autocorrelationSeries.getData().add(new XYChart.Data<>(i, autocorrelation[i]));
        }
        autocorrelationChart.getData().add(autocorrelationSeries);

        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(DftNormalization.STANDARD);
        Complex[] complex = fastFourierTransformer.transform(autocorrelation, TransformType.FORWARD);

        double[] spectrum = new double[complex.length];

        for (int i = 0; i < spectrum.length; i++) {
            spectrum[i] = Math.sqrt(Math.pow(complex[i].getReal(), 2) + Math.pow(complex[i].getImaginary(), 2));
        }

        XYChart.Series<Number, Number> spectrumSeries = new XYChart.Series<>();
        for (int i = 0; i < spectrum.length; i++) {
            spectrumSeries.getData().add(new XYChart.Data<>(i, spectrum[i]));
        }
        spectrumChart.getData().add(spectrumSeries);

//        Complex[] complex2 = fastFourierTransformer.transform(signalArray, TransformType.FORWARD);
//        XYChart.Series<Number, Number> spectrumSeries2 = new XYChart.Series<>();
//        for (int i = 0; i < spectrum.length; i++) {
//            double re = complex2[i].getReal();
//            double im = complex2[i].getImaginary();
//            double value = Math.sqrt(re * re + im * im);
//            spectrumSeries2.getData().add(new XYChart.Data<>(i, value));
//        }
//        spectrumChart.getData().add(spectrumSeries2);

    }
}
