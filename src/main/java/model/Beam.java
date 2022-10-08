package main.java.model;

public interface Beam {
    Photon getNextPhoton();

    boolean hasNextPhoton();

    long initSize();
}
