/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private boolean[][] openSites;
    private int n;
    private WeightedQuickUnionUF wquf;
    private int openCnt = 0;
    private int[] dx = new int[] { -1, 0, 1, 0 };
    private int[] dy = new int[] { 0, 1, 0, -1 };
    private final int OPEN_FLAG = 1, BOT_FLAG = 1 << 1, TOP_FLAG = 1 << 2;
    private final int PERCOLATION_COMPLETED = OPEN_FLAG | BOT_FLAG | TOP_FLAG;
    private boolean percolationFlag = false;
    /**
     * Each element of the array will store 3 bits.  00000111 top | bot | open
     * <p>
     * <li>One representing if the site is open.</li>
     * <li>Another representing if the subtree rooted at i is connected to the top through open
     * sites.</li>
     * <li>Another representing if the subtree rooted at i is connected to the bottom through open
     * sites.</li>
     * </p>
     */
    private byte[] states;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0)
            throw new IllegalArgumentException("n should be bigger than 0");
        openSites = new boolean[n + 1][n + 1];
        states = new byte[(n + 1) * (n + 1)];
        wquf = new WeightedQuickUnionUF(n * n + 1);
        this.n = n;
        // top
        for (int i = 1; i <= n; i++) {
            states[i] |= TOP_FLAG;
        }
        // bot
        for (int i = n * n; i > n * (n - 1); i--) {
            states[i] |= BOT_FLAG;
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);
        if (isOpen(row, col)) return;

        openSites[row][col] = true;
        int r, c;
        int cur1d = conv2dTo1d(row, col);
        states[cur1d] |= OPEN_FLAG;
        // check surrounding sites to union current site with those open sites
        for (int i = 0; i <= 3; i++) {
            r = row + dx[i];
            c = col + dy[i];
            if (r > 0 && r <= n && c > 0 && c <= n && isOpen(r, c)) {
                /**
                 * Set the bits appropriately when opening a previously blocked site.
                 * Update the data of the new root with the OR of the data of the previous roots,
                 * when performing the union() of two sites.
                 */
                int curP = wquf.find(cur1d), rcp = wquf.find(conv2dTo1d(r, c));
                states[curP] |= states[rcp];
                states[rcp] = states[curP];
                if (states[rcp] == PERCOLATION_COMPLETED)
                    percolationFlag = true;
                wquf.union(curP, rcp);
                // substitute WeightedQuickUnionUF data type that picks leader nondeterministically;
                // the new parent may not be one of the two, we should update it too.
                states[wquf.find(curP)] |= states[rcp];
            }
        }
        // corner case, n = 1 or n = 2
        if (states[cur1d] == PERCOLATION_COMPLETED)
            percolationFlag = true;
        openCnt++;
    }


    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openSites[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) return false;
        return (states[wquf.find(conv2dTo1d(row, col))] & TOP_FLAG) != 0;
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openCnt;
    }

    // does the system percolate?
    public boolean percolates() {
        if (openCnt == 0) return false;
        return percolationFlag;
    }

    private void validate(int row, int col) {
        if (row <= 0 || col <= 0)
            throw new IllegalArgumentException("row or col should be bigger than 0");
        if (row > this.n || col > this.n)
            throw new IllegalArgumentException("row or col should be smaller than " + this.n);
    }

    private int conv2dTo1d(int row, int col) {
        validate(row, col);
        return (row - 1) * this.n + col;
    }
}
