/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private int trials;
    private double[] fractions;
    private double mean;
    private double stddev;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        validate(n, trials);
        this.trials = trials;
        fractions = new double[trials];
        for (int i = 0; i < trials; i++) {
            Percolation perc = new Percolation(n);
            while (!perc.percolates()) {
                int x = StdRandom.uniformInt(1, n + 1);
                int y = StdRandom.uniformInt(1, n + 1);
                while (perc.isOpen(x, y)) {
                    x = StdRandom.uniformInt(1, n + 1);
                    y = StdRandom.uniformInt(1, n + 1);
                }
                perc.open(x, y);
            }
            fractions[i] = 1.0 * perc.numberOfOpenSites() / (n * n);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        this.mean = StdStats.mean(fractions);
        return this.mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        this.stddev = StdStats.stddev(fractions);
        return this.stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        if (this.mean == 0.0) this.mean = mean();
        if (this.stddev == 0.0) this.stddev = stddev();
        return this.mean - (1.96 * this.stddev / Math.sqrt(trials));
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        if (this.mean == 0.0) this.mean = mean();
        if (this.stddev == 0.0) this.stddev = stddev();
        return this.mean + (1.96 * this.stddev / Math.sqrt(trials));
    }

    private void validate(int n, int t) {
        if (n <= 0 || t <= 0)
            throw new IllegalArgumentException("row or col should be bigger than 0");
    }

    public static void main(String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException("please enter two args: n and T");
        int n = Integer.parseInt(args[0]);
        int t = Integer.parseInt(args[1]);
        PercolationStats percolationStats = new PercolationStats(n, t);
        StdOut.printf("%-30s = %.16f%n", "mean", percolationStats.mean());
        StdOut.printf("%-30s = %.16f%n", "stddev", percolationStats.stddev());
        StdOut.printf("%-30s = [%.16f, %.16f]%n", "95% confidence interval",
                      percolationStats.confidenceLo(), percolationStats.confidenceHi());
    }
}
