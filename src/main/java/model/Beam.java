package main.java.model;

public class Beam {
    private final long photonAmount;

    private final double meanRadius;

    public Beam(long photonAmount, double meanRadius) {
        this.photonAmount = photonAmount;
        this.meanRadius = meanRadius;
    }

    public long getPhotonAmount() {
        return photonAmount;
    }

    public double getMeanRadius() {
        return meanRadius;
    }
}
