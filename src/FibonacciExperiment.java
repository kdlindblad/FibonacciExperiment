import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;

public class FibonacciExperiment {

    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE =  2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 40;
    static int MAXINPUTSIZE  = (int) Math.pow(2,10);
    static int MININPUTSIZE  =  1;
    // static int SIZEINCREMENT =  10000000; // not using this since we are doubling the size each time

    static String ResultsFolderPath = "/home/karson/Results/"; // pathname to results folder
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main(String[] args) {

        // run the whole experiment at least twice, and expect to throw away the data from the earlier runs, before java has fully optimized
        runFullExperiment("FibRecur-Exp1-ThrowAway.txt");
        runFullExperiment("FibRecur-Exp2.txt");
        runFullExperiment("FibRecur-Exp3.txt");
    }

    static void runFullExperiment(String resultsFileName){

        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return; // not very foolproof... but we do expect to be able to create/open the file...
        }

        ThreadCpuStopWatch BatchStopwatch = new ThreadCpuStopWatch(); // for timing an entire set of trials
        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch(); // for timing an individual trial

        resultsWriter.println("#InputX  InputSizeN  AverageTime  trial"); // # marks a comment in gnuplot data
        resultsWriter.flush();
        /* for each size of input we want to test: in this case starting small and doubling the size each time */
        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*=2) {
            // progress message...
            System.out.println("Running test for input size "+inputSize+" ... ");

            /* repeat for desired number of trials (for a specific size of input)... */
            long batchElapsedTime = 0;
            // In this case we're generating one list to use for the entire set of trials (of a given input size)
            // but we will randomly generate the search key for each trial
            System.out.println("    ...done.");
            System.out.print("    Running trial batch...");

            /* force garbage collection before each batch of trials run so it is not included in the time */
            System.gc();


            // instead of timing each individual trial, we will time the entire set of trials (for a given input size)
            // and divide by the number of trials -- this reduces the impact of the amount of time it takes to call the
            // stopwatch methods themselves
            BatchStopwatch.start(); // comment this line if timing trials individually

            // run the trials
            long fibX=0;
            long trial = 0;
            for (trial = 0; trial < numberOfTrials; trial++) {
                /* force garbage collection before each trial run so it is not included in the time */
                // System.gc();

                //TrialStopwatch.start(); // *** uncomment this line if timing trials individually
                /* run the function we're testing on the trial input */


                //fibX = FibLoop(trial);
                fibX = FibRecur(trial);
                //fibX = FibRecurDP(trial);
                //fibX = FibMatrix(trial);

                // batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime(); // *** uncomment this line if timing trials individually
            }
            batchElapsedTime = BatchStopwatch.elapsedTime(); // *** comment this line if timing trials individually
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials; // calculate the average time per trial in this batch

            /* print data for this size of input */
            resultsWriter.printf("%12d %15d %17.2f %19d\n",fibX , inputSize, averageTimePerTrialInBatch, trial); // might as well make the columns look nice
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }

    public static long FibLoop(long x){
        long first = 0, next = 1, sum = 0;
        if(x == 0)
            return 0;
        else if( x == 1)
            return 1;
        else
            for(long i = 1; i <= x-2; i++){
            sum = first + next;
            first = next;
            next = sum;
            }
        return sum;
    }

    public static long FibRecur(long x){
        if(x == 0 || x == 1){
            return x;
        }
        else{
            return FibRecur(x-1) + FibRecur(x-2);
        }
    }

    public static long FibRecurDP(int x){
        long f[] = new long[x+2];
        f[0] = 0;
        f[1] = 1;
        for( int i = 2; i <= x; i++){
            f[i] = f[i-1]+f[i-2];
        }

        return f[x];
    }

    public static long FibMatrix(long x){
        long F[][] = new long[][]{{1,1},{1,0}};
        if(x == 0)
            return 0;
        power(F, x-1);
        return F[0][0];
    }

    public static void power(long F[][], long x){
        long i;
        long M[][] = new long[][]{{1,1},{1,0}};

        for(i = 2; i<=x; i++)
            multiply(F, M);
    }

    public static void multiply(long F[][], long M[][]){
        long x =  F[0][0]*M[0][0] + F[0][1]*M[1][0];
        long y =  F[0][0]*M[0][1] + F[0][1]*M[1][1];
        long z =  F[1][0]*M[0][0] + F[1][1]*M[1][0];
        long w =  F[1][0]*M[0][1] + F[1][1]*M[1][1];

        F[0][0] = x;
        F[0][1] = y;
        F[1][0] = z;
        F[1][1] = w;
    }
}
