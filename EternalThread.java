package concurrentcube;

import java.util.Random;

public class EternalThread extends Thread {
    Cube cube;
    Random rand = new Random();

    public EternalThread(Cube cube) {
        this.cube = cube;
    }

    public void run() {
        while(true) {
            try {
                cube.rotate(rand.nextInt(6), rand.nextInt(cube.getsize()));
            } catch (InterruptedException e) {
                break;
            }
            if (rand.nextInt(100) < 10) {
                try {
                    cube.show();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}
