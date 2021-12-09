package concurrentcube;
import java.util.concurrent.Semaphore;
import java.util.function.BiConsumer;

public class Cube {
    private final BiConsumer<Integer, Integer> beforeRotation;
    private final BiConsumer<Integer, Integer> afterRotation;
    private Runnable beforeShowing;
    private Runnable afterShowing;
    private static int[][][] cube;
    private final int size;
    // Czytelnicy i pisarze
    int n_rotate = 0, n_show = 0, w_rotate = 0, w_show = 0;
    Semaphore mutex1 = new Semaphore(1);
    Semaphore rotate_s = new Semaphore(0);
    Semaphore show_s = new Semaphore(0);
    // Synchronizacja grupowa
    Semaphore mutex2 = new Semaphore(1);
    Semaphore first = new Semaphore(0);
    Semaphore[] wait = new Semaphore[] {
            new Semaphore(0),
            new Semaphore(0),
            new Semaphore(0)
    };
    Semaphore end = new Semaphore(0);
    Semaphore[] layer_s;
    int who = -1;
    int n_work = 0;
    int[] n_wait = new int[3];
    int n_end = 0;
    int n_group = 0;


    public Cube(int size,
                BiConsumer<Integer, Integer> beforeRotation,
                BiConsumer<Integer, Integer> afterRotation,
                Runnable beforeShowing,
                Runnable afterShowing) {
        this.beforeRotation = beforeRotation;
        this.afterRotation = afterRotation;
        this.beforeShowing = beforeShowing;
        this.afterShowing = afterShowing;
        this.size = size;
        cube = new int[6][size][size];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < size; j++)
                for (int k = 0; k < size; k++)
                    cube[i][j][k] = i;

        layer_s = new Semaphore[size];
        for (int i = 0; i < size; i++)
            layer_s[i] = new Semaphore(1);
    }
    // Zwraca numer przeciwnej strony kostki
    private int backside(int side) {
        int back = side;
        switch (side) {
            case 0:
                back = 5;
                break;
            case 1:
                back = 3;
                break;
            case 2:
                back = 4;
                break;
            case 3:
                back = 1;
                break;
            case 4:
                back = 2;
                break;
            case 5:
                back = 0;
                break;
        }
        return back;
    }
    // Zwraca numer grupy wątku
    private int group_number(int side) {
        if (side <= 2)
            return side;
        else return backside(side);
    }
    // Obraca kostkę
    private void rotation(int side, int layer) {
        switch(side) {
            case 0:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[3][layer][size - i - 1];
                    cube[3][layer][size - i - 1] = cube[4][layer][size - i - 1];
                    cube[4][layer][size - i - 1] = cube[1][layer][size - i - 1];
                    cube[1][layer][size - i - 1] = cube[2][layer][size - i - 1];
                    cube[2][layer][size - i - 1] = bufor;
                }
                break;
            case 1:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[2][i][layer];
                    cube[2][i][layer] = cube[0][i][layer];
                    cube[0][i][layer] = cube[4][size - i - 1][size - layer - 1];
                    cube[4][size - i - 1][size - layer - 1] = cube[5][i][layer];
                    cube[5][i][layer] = bufor;
                }
                break;
            case 2:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[3][i][layer];
                    cube[3][i][layer] = cube[0][size - layer - 1][i];
                    cube[0][size - layer - 1][i] = cube[1][size - i - 1][size - layer - 1];
                    cube[1][size - i - 1][size - layer - 1] = cube[5][layer][size - i - 1];
                    cube[5][layer][size - i - 1] = bufor;
                }
                break;
            case 3:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[4][i][layer];
                    cube[4][i][layer] = cube[0][size - i - 1][size - layer - 1];
                    cube[0][size - i - 1][size - layer - 1] = cube[2][size - i - 1][size - layer - 1];
                    cube[2][size - i - 1][size - layer - 1] = cube[5][size - i - 1][size - layer - 1];
                    cube[5][size - i - 1][size - layer - 1] = bufor;
                }
                break;
            case 4:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[1][i][layer];
                    cube[1][i][layer] = cube[0][layer][size - i - 1];
                    cube[0][layer][size - i - 1] = cube[3][size - i - 1][size - layer - 1];
                    cube[3][size - i - 1][size - layer - 1] = cube[5][size - layer - 1][i];
                    cube[5][size - layer - 1][i] = bufor;
                }
                break;
            case 5:
                for (int i = 0; i < size; i++) {
                    int bufor = cube[3][size - layer - 1][i];
                    cube[3][size - layer - 1][i] = cube[2][size - layer - 1][i];
                    cube[2][size - layer - 1][i] = cube[1][size - layer - 1][i];
                    cube[1][size - layer - 1][i] = cube[4][size - layer - 1][i];
                    cube[4][size - layer - 1][i] = bufor;
                }
                break;
        }
        if (layer == 0) {
            int[][] bufor = new int[size][size];
            for (int i = 0; i < size; i++)
                System.arraycopy(cube[side][i], 0, bufor[i], 0, size);

            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    cube[side][j][size - i - 1] = bufor[i][j];
        }
        else if (layer == size - 1) {
            int[][] bufor = new int[size][size];
            int back = backside(side);

            for (int i = 0; i < size; i++)
                System.arraycopy(cube[back][i], 0, bufor[i], 0, size);

            for (int i = 0; i < size; i++)
                for (int j = 0; j < size; j++)
                    cube[back][i][j] = bufor[j][size - i - 1];
        }
    }
    // Synchronizuje grupowo wątki chcące obracać kostkę
    private void rotation_group(int side, int layer) throws InterruptedException {
        int gr = group_number(side);
        mutex2.acquireUninterruptibly();
        try {
            if (who == -1)
                who = gr;
            else {
                if (who != gr) {
                    n_wait[gr]++;
                    if (n_wait[gr] == 1) {
                        n_group++;
                        mutex2.release();
                        first.acquireUninterruptibly();
                        n_group--;
                        who = gr;
                    }
                    else {
                        mutex2.release();
                        wait[gr].acquireUninterruptibly();
                    }
                    n_wait[gr]--;
                }
            }
            n_work++;
        }
        finally {
            if (n_wait[gr] > 0)
                wait[gr].release();
            else
                mutex2.release();
        }

        if (!Thread.currentThread().isInterrupted()) {
            if (side == gr)
                layer_s[layer].acquireUninterruptibly();
            else layer_s[size - layer - 1].acquireUninterruptibly();
            rotation(side, layer);
            if (side == gr)
                layer_s[layer].release();
            else layer_s[size - layer - 1].release();
        }

        mutex2.acquireUninterruptibly();
        n_work--;
        if (n_work > 0) {
            n_end++;
            mutex2.release();
            try {
                end.acquireUninterruptibly();
            }
            finally {
                n_end--;
            }
        }
        if (n_end > 0)
            end.release();
        else {
            if (n_group > 0)
                first.release();
            else {
                who = -1;
                mutex2.release();
            }
        }
    }
    // Rozwiązuje problem czytelników (rotate) i pisarzy (show)
    public void rotate(int side, int layer) throws InterruptedException {
        mutex1.acquire();
        if (n_show + w_show > 0) {
            w_rotate++;
            mutex1.release();
            rotate_s.acquireUninterruptibly();
            w_rotate--;
        }
        n_rotate++;
        if (w_rotate > 0)
            rotate_s.release();
        else
            mutex1.release();
        if(!Thread.currentThread().isInterrupted()) {
            beforeRotation.accept(side, layer);
            rotation_group(side, layer);
            afterRotation.accept(side, layer);
        }
        mutex1.acquireUninterruptibly();
        n_rotate--;
        if ((n_rotate == 0) && (w_show > 0))
            show_s.release();
        else
            mutex1.release();

        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();
    }
    // Tworzy string ze stanem kostki
    private String showing() {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++)
                    result.append(cube[i][j][k]);
            }
        return result.toString();
    }
    // Rozwiązuje problem czytelników (rotate) i pisarzy (show)
    public String show() throws InterruptedException {
        String result = " ";
        mutex1.acquire();
        if (n_show + n_rotate > 0) {
            w_show++;
            mutex1.release();
            show_s.acquireUninterruptibly();
            w_show--;
        }
        n_show++;
        mutex1.release();
        if(!Thread.currentThread().isInterrupted()) {
            beforeShowing.run();
            result = showing();
            afterShowing.run();
        }
        mutex1.acquireUninterruptibly();
        n_show--;
        if (w_rotate > 0)
            rotate_s.release();
        else if (w_show > 0)
            show_s.release();
        else
            mutex1.release();


        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException();

        return result;
    }

    public int getsize() {
        return size;
    }
    public int getvalue(int side, int row, int column) {
        return  cube[side][row][column];
    }
}
