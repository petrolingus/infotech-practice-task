package me.petrolingus.infotechpracticetask.domain.generator;

import me.petrolingus.infotechpracticetask.domain.Harmonic;

public class DiscreteSignalGenerator {

    private final int samplesCount;

    private final double samplingFrequency;

    private final Harmonic[] harmonics;

    public DiscreteSignalGenerator(int samplesCount, double samplingFrequency, Harmonic[] harmonics) {
        this.samplesCount = samplesCount;
        this.samplingFrequency = samplingFrequency;
        this.harmonics = harmonics;
    }

    public double[] generate() {

        double[] samples = new double[samplesCount];

        for (int i = 0; i < samplesCount; i++) {
            double x = i * samplingFrequency;
            for (Harmonic harmonic : harmonics) {
                samples[i] += harmonic.value(x);
            }
        }

        return samples;
    }
}
