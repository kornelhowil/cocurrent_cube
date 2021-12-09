package concurrentcube;
import org.junit.jupiter.api.*;
import java.util.Random;

public class CubeTest {
    /*
    Sprawdza poprawność obrotów kostki
     */
    @Test
    void RotationTest() throws InterruptedException {
        System.out.println("Rotation Test");
        Cube cube = new Cube(1,
                (x, y) -> {
                },
                (x, y) -> {
                },
                () -> {
                },
                () -> {
                }
        );
        cube.rotate(0, 0);
        cube.rotate(1, 0);
        cube.rotate(2, 0);
        cube.rotate(3, 0);
        cube.rotate(4, 0);
        cube.rotate(5, 0);
        Assertions.assertEquals("120453", cube.show());

        Cube cube2 = new Cube(2,
                (x, y) -> {
                },
                (x, y) -> {
                },
                () -> {
                },
                () -> {
                }
        );
        cube2.rotate(0, 0);
        cube2.rotate(1, 1);
        cube2.rotate(2, 0);
        cube2.rotate(3, 1);
        cube2.rotate(4, 0);
        cube2.rotate(5, 1);
        Assertions.assertEquals("323055214350431102014452", cube2.show());

        Cube cube3 = new Cube(3,
                (x, y) -> {
                },
                (x, y) -> {
                },
                () -> {
                },
                () -> {
                }
        );
        cube3.rotate(0, 0);
        cube3.rotate(1, 1);
        cube3.rotate(2, 2);
        cube3.rotate(3, 0);
        cube3.rotate(4, 1);
        cube3.rotate(5, 2);
        cube3.rotate(0, 1);
        cube3.rotate(1, 2);
        cube3.rotate(2, 0);
        cube3.rotate(3, 1);
        cube3.rotate(4, 2);
        cube3.rotate(5, 0);
        Assertions.assertEquals("332122133044201144522013501550455200154131300325543442", cube3.show());


        Cube cube4 = new Cube(10,
                (x, y) -> {
                },
                (x, y) -> {
                },
                () -> {
                },
                () -> {
                }
        );
        for (int i = 0; i < 25; i++) {
            cube4.rotate(0, 6);
            cube4.rotate(1, 7);
            cube4.rotate(3, 0);
            cube4.rotate(5, 1);
            cube4.rotate(4, 0);
            cube4.rotate(2, 7);
        }
        Assertions.assertEquals("40343344330000000304240033334200000004030000000404" +
                "00000004040000000003000000020300000003000000000502" +
                "01011111113131111111410111111141411111113131111111" +
                "31311111114442332111314111111114133331414101111111" +
                "22222221232222222424222222252422222224242222222424" +
                "22222224242423443242222222252321234432122222222125" +
                "03205505140354442434534500521033344433331411334313" +
                "14113343133134443313504055350432334424242515003455" +
                "02304405413334334540320544534044413341110253443035" +
                "02534430352321224141503422030233323324443504225155" +
                "55555550535555555453555555515455555553545555555454" +
                "55555554545555555355431433445355555555531443335344", cube4.show());

        System.out.println("Rotation test passed :)");
    }
    /*
    Sprawdza bezpieczeństwo poprzez sprawdzenie czy manipulacje wielu wątków prowadzą do niepoprawnego stanu kostki
     */
    @Test
    void SafetyTest() {
        System.out.println("Safety Test");
        HelpTest help = new HelpTest();
        Random random = new Random();

        int n_tries = 100;
        int max_rotations = 500;
        int max_threads = 100;
        int max_cubesize = 50;
        for (int i = 0; i < n_tries; i++) {
            Cube cube = new Cube(random.nextInt(max_cubesize) + 1,
                    (x, y) -> {
                    },
                    (x, y) -> {
                    },
                    () -> {
                    },
                    () -> {
                    }
            );
            CubeThread[] threads = new CubeThread[random.nextInt(max_threads) + 1];
            for (int j = 0; j < threads.length; j++) {
                threads[j] = new CubeThread(cube, random.nextInt(max_rotations) + 1);
                threads[j].start();
            }
            for (CubeThread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Assertions.assertTrue(help.checkcolors(cube));
            System.out.println(i + 1 + "/" + n_tries);
        }
        System.out.println("Safety Test passed :)");
    }
    /*
    Sprawdza czy przerwany wątek zwraca InterruptedException
     */
    @Test
    void InterruptTest() throws InterruptedException {
        System.out.println("Interrupt Test");
        HelpTest help = new HelpTest();
        Random random = new Random();

        int n_tries = 100;
        int max_threads = 100;
        int max_cubesize = 50;
        for (int i = 0; i < n_tries; i++) {
            Cube cube = new Cube(random.nextInt(max_cubesize) + 1,
                    (x, y) -> {
                    },
                    (x, y) -> {
                    },
                    () -> {
                    },
                    () -> {
                    }
            );
            EternalThread[] threads = new EternalThread[random.nextInt(max_threads) + 1];
            for (int j = 0; j < threads.length; j++) {
                threads[j] = new EternalThread(cube);
                threads[j].start();
            }

            Thread.sleep(500);

            for (EternalThread thread : threads) {
                thread.interrupt();
            }
            for (EternalThread thread : threads) {
                thread.join();
            }
            Assertions.assertTrue(help.checkcolors(cube));
            System.out.println(i + 1 + "/" + n_tries);
        }
        System.out.println("Interrupt Test passed :)");
    }
    /*
    Sprawdza czy przerwany wątek sprząta po sobie w sposób poprawny
     */
    @Test
    void InterruptTest2() throws InterruptedException {
        System.out.println("Interrupt Test 2");
        HelpTest help = new HelpTest();
        Random random = new Random();

        int n_tries = 100;
        int max_rotations = 500;
        int max_threads = 100;
        int max_cubesize = 50;
        for (int i = 0; i < n_tries; i++) {
            Cube cube = new Cube(random.nextInt(max_cubesize) + 1,
                    (x, y) -> {
                    },
                    (x, y) -> {
                    },
                    () -> {
                    },
                    () -> {
                    }
            );
            EternalThread[] threads = new EternalThread[random.nextInt(max_threads) + 1];
            for (int j = 0; j < threads.length; j++) {
                threads[j] = new EternalThread(cube);
                threads[j].start();
            }
            CubeThread[] cubethreads = new CubeThread[random.nextInt(max_threads) + 1];
            for (int j = 0; j < cubethreads.length; j++) {
                cubethreads[j] = new CubeThread(cube, random.nextInt(max_rotations));
                cubethreads[j].start();
            }
            Thread.sleep(500);

            for (EternalThread thread : threads) {
                thread.interrupt();
            }
            for (EternalThread thread : threads) {
                thread.join();
            }
            for (CubeThread thread : cubethreads) {
                thread.join();
            }
            Assertions.assertTrue(help.checkcolors(cube));
            System.out.println(i + 1 + "/" + n_tries);
        }
        System.out.println("Interrupt Test passed :)");
    }
}