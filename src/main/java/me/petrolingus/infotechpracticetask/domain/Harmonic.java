package me.petrolingus.infotechpracticetask.domain;

public class Harmonic {

    private final double amplitude;

    private final double frequency;

    private final double phase;

    public Harmonic(double amplitude, double frequency, double phase) {
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phase = phase;
    }

    public double value(double x) {
        return amplitude * Math.sin(2 * Math.PI * frequency * x + phase);
    }
}
