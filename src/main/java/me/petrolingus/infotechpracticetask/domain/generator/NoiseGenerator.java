package me.petrolingus.infotechpracticetask.domain.generator;

import java.util.Arrays;
import java.util.Random;

public class NoiseGenerator {

    private final double[] signal;

    private static final Random random = new Random();

    public NoiseGenerator(double[] signal) {
        this.signal = Arrays.copyOf(signal, signal.length);
    }

    public double[] generate(double noiseCoefficient) {

        double[] noise = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {
            noise[i] = random.nextGaussian();
        }

        double noiseEnergy = calculateEnergy(noise);
        double signalEnergy = calculateEnergy(signal);

        double alpha = Math.sqrt((noiseCoefficient / 100) * signalEnergy / noiseEnergy);

        double[] result = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {
            result[i] = signal[i] + alpha * noise[i];
        }

        return result;
    }

    private double calculateEnergy(double[] signal) {
        double energy = 0;
        for (int i = 0; i < signal.length; i++) {
            energy += signal[i] * signal[i];
        }
        return energy;
    }

}
