package me.petrolingus.infotechpracticetask.domain.generator;

import java.util.Arrays;
import java.util.Random;

public class NoiseGenerator {

    private final double[] signal;

    private static final Random random = new Random();

    public NoiseGenerator(double[] signal) {
        this.signal = Arrays.copyOf(signal, signal.length);
    }

    public double[] generate() {

        double[] noise = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {
            noise[i] = random.nextGaussian();
        }

        double noiseEnergy = calculateEnergy(noise);



        return new double[100];
    }

    private double calculateEnergy(double[] signal) {
        double energy = 0;
        for (int i = 0; i < signal.length; i++) {
            energy += signal[i] * signal[i];
        }
        return energy;
    }

}
