package concurrentcube;

public class HelpTest {
    public HelpTest() {
    }

    public boolean checkcolors(Cube cube){
        int size = cube.getsize();
        int[] colors = new int[6];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < size; j++)
                for (int k = 0; k < size; k++)
                    colors[cube.getvalue(i,j,k)] += 1;

        for (int i = 0; i < 6; i++)
            if (colors[i] != size*size)
                return false;
        return true;
    }
}
