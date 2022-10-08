package main.java;

import main.java.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException, IOException {
        int rMax = Integer.parseInt(args[0]); //50
        int zMax = Integer.parseInt(args[1]); //100
        long num_photons = Integer.parseInt(args[2]); //1_000_000_000
        double meanRadius = Double.parseDouble(args[3]); //25
        int avgDist = Integer.parseInt(args[4]); //25
        double absCoff = Double.parseDouble(args[5]); //0.002
        double n1 = Double.parseDouble(args[6]); //1
        double n2 = Double.parseDouble(args[7]); //1.33
        Path filePath1 = Paths.get(args[8]); //store grid with accumulated photons
        Path filePath2 = Paths.get(args[9]); //store scattered photons

        long start = System.currentTimeMillis();
        Grid grid = new Grid(rMax, zMax);
        int availableProc = Runtime.getRuntime().availableProcessors();
        Material material = new PorousMaterial(absCoff, avgDist, n1, n2);
        ExecutorService service = Executors.newFixedThreadPool(availableProc);
        List<Callable<int[]>> tasks = new ArrayList<>();
        for (int i = 0; i < availableProc; i++) {
            Beam beam = new GaussBeam(num_photons / availableProc, meanRadius);
            tasks.add(new BeamPropagationTask(beam, grid, material));
        }
        List<Future<int[]>> futures = service.invokeAll(tasks);
        service.shutdown();
        long end = System.currentTimeMillis();
        long elapsedTime = end - start;

        long accum_photons = grid.calcAccumPhotons();
        long lost_photons = 0;
        int[] scatteredPhotons = new int[100];
        for (Future<int[]> future : futures) {
            int[] ints = future.get();
            lost_photons += Arrays.stream(ints).reduce(0, Integer::sum);
            for (int i = 0; i < 100; i++) {
                scatteredPhotons[i] += ints[i];
            }
        }

        long registered_photons_count = accum_photons + lost_photons;
        System.out.printf("Elapsed time %d s%n", elapsedTime/1000);
        System.out.printf("Registered photons %s%n", registered_photons_count);
        double ratio = (double) lost_photons / registered_photons_count;
        System.out.printf("Lost photons ratio %f%n", ratio);

        int[][] accumArray = grid.getAccumArray();
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath1)) {
            for (int[] ints : accumArray) {
                String s = Arrays.toString(ints);
                s = s.replaceAll("[],\\[]", "");
                bufferedWriter.write(s);
                bufferedWriter.newLine();
            }
        }

        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(filePath2)) {
            double teta = 0;
            double delta = Math.PI / 100;
            for (int scattered : scatteredPhotons) {
                String s = String.format("%f %d", teta, scattered);
                bufferedWriter.write(s);
                bufferedWriter.newLine();
                teta += delta;
            }
        }
    }
}
