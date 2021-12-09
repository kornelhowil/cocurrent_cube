package concurrentcube;

import java.util.Random;

public class CubeThread extends Thread {
    Cube cube;
    int n_rotation;
    Random rand = new Random();

    public CubeThread(Cube cube, int n_rotation) {
        this.cube = cube;
        this.n_rotation = n_rotation;
    }

    public void run() {
        for (int i = 0; i < n_rotation; i++) {
            try {
                cube.rotate(rand.nextInt(6), rand.nextInt(cube.getsize()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (rand.nextInt(100) < 10) {
                try {
                    cube.show();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
