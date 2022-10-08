package main.java.model;

public class Photon {

    public Photon() {
    }

    public Photon(double r) {
        this.r = r;
    }

    private double teta = 0;

    private double r = 0;

    private double z = 0;

    public void updatePosition(double deltaL, double deltaTeta) {
        teta += deltaTeta;
        r += deltaL * Math.sin(teta);
        if (r < 0) {
            r = Math.abs(r);
            teta = -teta;
        }
        z += deltaL * Math.cos(teta);
    }


    public double getTeta() {
        return teta;
    }

    public void setTeta(double teta) {
        this.teta = teta;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

}
