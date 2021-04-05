package me.petrolingus.infotechpracticetask.domain.generator;

import me.petrolingus.infotechpracticetask.domain.Harmonic;

public class HarmonicGenerator {

    private final double minAmplitude;
    private final double maxAmplitude;

    private final double minFrequency;
    private final double maxFrequency;

    private final double minPhase;
    private final double maxPhase;

    private final int harmonicsCount;

    public HarmonicGenerator(double minAmplitude, double maxAmplitude,
                             double minFrequency, double maxFrequency,
                             double minPhase, double maxPhase, int harmonicsCount) {
        this.minAmplitude = minAmplitude;
        this.maxAmplitude = maxAmplitude;
        this.minFrequency = minFrequency;
        this.maxFrequency = maxFrequency;
        this.minPhase = minPhase;
        this.maxPhase = maxPhase;
        this.harmonicsCount = harmonicsCount;
    }

    public Harmonic[] generate() {

        double amplitudeStep = (maxAmplitude - minAmplitude) / (harmonicsCount - 1);
        double frequencyStep = (maxFrequency - minFrequency) / (harmonicsCount - 1);
        double phaseStep = (maxPhase - minPhase) / (harmonicsCount - 1);

        amplitudeStep = (harmonicsCount < 2) ? minAmplitude : amplitudeStep;
        frequencyStep = (harmonicsCount < 2) ? minFrequency : frequencyStep;
        phaseStep = (harmonicsCount < 2) ? minPhase : phaseStep;

        Harmonic[] harmonics = new Harmonic[harmonicsCount];

        for (int i = 0; i < harmonicsCount; i++) {
            double amplitude = minAmplitude + i * amplitudeStep;
            double frequency = minFrequency + i * frequencyStep;
            double phase = minPhase + i * phaseStep;
            harmonics[i] = new Harmonic(amplitude, frequency, phase);
        }

        return harmonics;
    }
}
