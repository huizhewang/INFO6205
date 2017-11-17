import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class PSO {

    private int bestNum;
    private float w;                // Inertia Weight
    private int MAX_GEN;            // Iteration Number
    private int scale;              // Particle Scale
    private int cNum;               // Customer Number
    private int t;                  // Current Generation
    private int begin;              //Begin Number

    private int[][] distance;       // Distance Matrix
    private int[][] Particle;       // Particle Swarm
    private ArrayList<ArrayList<SO>> listV;      // Original exchange sequence of particles
    private int[][] pBest;          // The historical best of every particle
    private int[] eP;               // Evaluation of Disaggregation
    private int[] gBest;            // Global Bests
    private int eG;                 // Evaluation of Global Bests
    private int bestT;              // the Generation of Best Length
    private int[] fitness;         // particle's fitness evaluation

    private Random random;

    ExecutorService executorService = Executors.newFixedThreadPool(50);

    public int[] getgBest() {
        return gBest;
    }

    public void setgBest(int[] gBest) {
        this.gBest = gBest;
    }


    public int geteG() {
        return eG;
    }

    public void seteG(int eG) {
        this.eG = eG;
    }

    public PSO(int cNum, int g, int s, float w, int b) {
        this.cNum = cNum;
        this.MAX_GEN = g;
        this.scale = s;
        this.w = w;
        this.begin = b;
    }

    /**
     * Initialize the PSO algorthm
     * @param filename coordinate data
     * @throws IOException
     */
    public void init(String filename) throws IOException {
        // read data
        int[] x;
        int[] y;
        String strbuff;
        BufferedReader data = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename)));
        distance = new int[cNum][cNum];
        x = new int[cNum];
        y = new int[cNum];
        for (int i = 0; i < cNum; i++) {
            strbuff = data.readLine();
            String[] strcol = strbuff.split(" ");
            x[i] = Integer.valueOf(strcol[1]);// x-position
            y[i] = Integer.valueOf(strcol[2]);// y-position
        }

        // calculate the distance matrix
        for (int i = 0; i < cNum - 1; i++) {
            distance[i][i] = 0;
            for (int j = i + 1; j < cNum; j++) {
                double rij = Math.sqrt(((x[i] - x[j]) * (x[i] - x[j]) + (y[i] - y[j]) * (y[i] - y[j])) / 10.0);
                // rounding-off
                int tij = (int) Math.round(rij);
                if (tij < rij) {
                    distance[i][j] = tij + 1;
                    distance[j][i] = distance[i][j];
                } else {
                    distance[i][j] = tij;
                    distance[j][i] = distance[i][j];
                }
            }
        }
        distance[cNum - 1][cNum - 1] = 0;

        Particle = new int[scale][cNum];
        fitness = new int[scale];

        pBest = new int[scale][cNum];
        eP = new int[scale];

        gBest = new int[cNum];
        eG = Integer.MAX_VALUE;


        bestT = 0;
        t = 0;

        random = new Random(System.currentTimeMillis());
    }

    // Initialize particle Swarm
    public void initGroup() {
        int i, j, k;
        // scale number
        for (k = 0; k < scale; k++) {
            //begin index
            Particle[k][0] = begin;

            // particle number
            for (i = 1; i < cNum;) {
                Particle[k][i] = random.nextInt(65535) % cNum;
                for (j = 0; j < i; j++) {
                    if (Particle[k][i] == Particle[k][j] || Particle[k][i] == begin) {
                        break;
                    }
                }
                if (j == i) {
                    i++;
                }
            }
        }
    }


    // Find num in arr[], return index
    public int findNum(int[] arr, int num) {
        int index = -1;
        for (int i = 0; i < cNum; i++) {
            if (arr[i] == num) {
                index = i;
                break;
            }
        }
        return index;
    }

    // swap index1 and index2 in arr[]
    public void changeIndex(int[] arr, int index1, int index2) {
        int temp = arr[index1];
        arr[index1] = arr[index2];
        arr[index2] = temp;
    }

    // A-B=SS
    public ArrayList<SO> minus(int[] a, int[] b) {
        int[] temp = b.clone();
        int index;
        ArrayList<SO> list = new ArrayList<SO>();
        for (int i = 0; i < cNum; i++) {
            if (a[i] != temp[i]) {
                // find index in temp, whose numerical value = a[i]
                index = findNum(temp, a[i]);
                // change i and index in temp
                changeIndex(temp, i, index);
                // save SO(X,Y)
                list.add(new SO(i, index));
            }
        }
        return list;
    }

    // original swap sequence of every particle
    public void initListV() {

        int rand, raA, raB;

        listV = new ArrayList<ArrayList<SO>>();

        for (int i = 0; i < scale; i++) {
            ArrayList<SO> list = new ArrayList<SO>();
            rand = random.nextInt(65535) % cNum;

            for (int j = 0; j < rand; j++) {
                raA = random.nextInt(65535) % cNum;
                while (raA == 0) {
                    raA = random.nextInt(65535) % cNum;
                }
                raB = random.nextInt(65535) % cNum;
                while (raA == raB || raB == 0) {
                    raB = random.nextInt(65535) % cNum;
                }
                //index
                list.add(new SO(raA, raB));
            }
            listV.add(list);
        }
    }

    public int evaluate(int[] chr) {
        int len = 0;
        // customer sequence, cNum 0, cNum 1, cNum 2 ... cNum n
        for (int i = 1; i < cNum; i++) {
            len += distance[chr[i - 1]][chr[i]];
        }
        // cNum n, cNum 0
        len += distance[chr[cNum - 1]][chr[0]];
        return len;
    }

    // Sequence after a basic swap
    public void add(int[] arr, ArrayList<SO> list) {
        int temp = -1;
        SO SO;
        for (int i = 0; i < list.size(); i++) {
            SO = list.get(i);
            temp = arr[SO.getX()];
            arr[SO.getX()] = arr[SO.getY()];
            arr[SO.getY()] = temp;
        }
    }

    // Copy a two-dimensional array
    public void copyarray(int[][] from, int[][] to) {
        for (int i = 0; i < scale; i++) {
            for (int j = 0; j < cNum; j++) {
                to[i][j] = from[i][j];
            }
        }
    }

    // Copy a one-dimensional array
    public void copyarrayNum(int[] from, int[] to) {
        for (int i = 0; i < cNum; i++) {
            to[i] = from[i];
        }
    }

    public void evolution() {
        int i, j, k;
        int len = 0;
        float ra = 0f;

        // a swap array of every particle
        ArrayList<SO> Vi;

        // iteration
        for (t = 0; t < MAX_GEN; t++) {
            // calculate particle routes using multithreading
            ArrayList<Callable<Void>> runnables = new ArrayList<>();
            // Every particle
            for (i = 0; i < scale; i++) {
                if(i == bestNum)
                    continue;
                final int ii = i;
                runnables.add(new Callable<Void>() {
                    @Override
                    public Void call() {
                        search(ii);
                        return null;
                    }
                });

            }
            try {
                List<Future<Void>> futures = executorService.invokeAll(runnables);
                for (Future<Void> future : futures) {
                    future.get();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // calculate fitness of new particle swarm，Fitness[max], pBest -> gBest
            for (k = 0; k < scale; k++) {
                fitness[k] = evaluate(Particle[k]);
                if (eP[k] > fitness[k]) {
                    eP[k] = fitness[k];
                    copyarrayNum(Particle[k], pBest[k]);
                    bestNum=k;
                }
                if (eG > eP[k]) {
                    System.out.println("Best Length: "+ eG +" is appeared at the Generation："+bestT);
                    bestT = t;
                    eG = eP[k];
                    copyarrayNum(pBest[k], gBest);
                }
            }
        }
    }

    private void search(int i) {
        int len, j;
        float ra = 0f;
        float rb = 0f;
        ArrayList<SO> Vi;
        ArrayList<SO> Vii = new ArrayList<SO>();

        // update speed

        // Vii=wVi+c1*ra*(Pid-Xid)+c2*rb*(Pgd-Xid)
        Vi = listV.get(i);

        // get SO(X,Y) of particle i
        // wVi+
        len = (int) (Vi.size() * w);

        for (j = 0; j < len; j++) {
            Vii.add(Vi.get(j));
        }

        // Pid-Xid
        ArrayList<SO> a = minus(pBest[i], Particle[i]);
        ra = random.nextFloat();

        // ra(Pid-Xid)+
        len = (int) (a.size() * ra);
        for (j = 0; j < len; j++) {
            Vii.add(a.get(j));
        }

        // Pgd-Xid
        ArrayList<SO> b = minus(gBest, Particle[i]);
        rb = random.nextFloat();

        // rb(Pid-Xid)+
        len = (int) (b.size() * rb);
        for (j = 0; j < len; j++) {
            SO tt= b.get(j);
            Vii.add(tt);
        }

        // Save new Vii
        listV.set(i, Vii);

        // update location
        // Xid’=Xid+Vid
        add(Particle[i], Vii);
    }

    public void solve() {
        int i;
        int k;

        initGroup();
        initListV();

        // pBest for every particle
        copyarray(Particle, pBest);

        // calculate fitness of original swarm，Fitness[max], gBest
        for (k = 0; k < scale; k++) {
            fitness[k] = evaluate(Particle[k]);
            eP[k] = fitness[k];
            if (eG > eP[k]) {
                eG = eP[k];
                copyarrayNum(pBest[k], gBest);
                bestNum=k;
            }
        }

        System.out.println("Initialize Particle Swarm");
        for (k = 0; k < scale; k++) {
            for (i = 0; i < cNum; i++) {
                System.out.print(Particle[k][i] + " ");
            }
            System.out.println();
            System.out.println("Fitness: " + fitness[k]);
        }

        evolution();

        System.out.println("Now, Particle Swarm");
        for (k = 0; k < scale; k++) {
            for (i = 0; i < cNum; i++) {
                System.out.print(Particle[k][i] + " ");
            }
            System.out.println();
            System.out.println("Fitness: " + fitness[k]);
        }
        System.out.println("----------------------------------------" );
        System.out.println("Best Generation：" + bestT);
        System.out.println("Best Length: " + eG);
        System.out.println("Best Route：");
        for (i = 0; i < cNum; i++) {
            System.out.print(gBest[i] + " ");
        }
        System.out.println();
        System.out.println();
    }

}
