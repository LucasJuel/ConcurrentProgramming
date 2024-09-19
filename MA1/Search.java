/*
 * 02158 Concurrent Programming
 * Mandatory Assignment 1
 * Version 1.3
 */


import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.*;

/**
 * Search task. No need to modify.
 */
class SearchTask implements Callable<List<Integer>> {

    char[] text, pattern;
    int from = 0, to = 0; // Searched string: text[from..(to-1)]

    /**
     * Create a task for searching occurrences of 'pattern' in the substring
     * text[from..(to-1)]
     */
    public SearchTask(char[] text, char[] pattern, int from, int to) {
        this.text = text;
        this.pattern = pattern;
        this.from = from;
        this.to = to;
    }

    public List<Integer> call() {
        final int pl = pattern.length;
        List<Integer> result = new LinkedList<Integer>();

        // VERY naive string matching to consume some CPU-cycles
        for (int i = from; i <= to - pl; i++) {
            boolean eq = true;
            for (int j = 0; j < pl; j++) {
                if (text[i + j] != pattern[j])
                    eq = false; // We really should break here
            }
            if (eq)
                result.add(i);
        }

        return result;
    }
}


enum Mode { SINGLE, CACHED, FIXED };


public class Search {
    
    enum Mode { SINGLE, CACHED, FIXED };

    static final int max = 10000000;            // Max no. of chars searched

    static char[] text = new char[max];         // file to be searched
    static int len;                             // Length of actual text
    static String fname;                        // Text file name
    static char[] pattern;                      // Search pattern
    static int ntasks = 5;                      // No. of tasks
    static int nthreads = 1;                    // No. of threads to use
    static boolean printPos = false;            // Print all positions found
    static int warmups = 0;                     // No. of warmup searches
    static int runs = 1;                        // No. of search repetitions
    static String  datafile;                    // Name of data file
    static Mode execMode = Mode.SINGLE;         // Kind of executor   

    static void getArguments(String[] argv) {
        // Reads arguments into static variables
        try {
            int i = 0;

            if (argv.length < 2)
                throw new Exception("Too few arguments");

            while (i < argv.length) {

                /* Check for options */
                if (argv[i].equals("-P")) {
                    printPos = true;
                    i++;
                    continue;
                }

                if (argv[i].equals("-R")) {
                    runs = Integer.valueOf(argv[i+1]);
                    i += 2;
                    continue;
                }

                if (argv[i].equals("-W")) {
                    warmups = Integer.valueOf(argv[i+1]);
                    i += 2;
                    continue;
                }

                if (argv[i].equals("-d")) {
                    datafile = argv[i+1];
                    i += 2;
                    continue;
                }

                if (argv[i].equals("-Es")) {
                    execMode = Mode.SINGLE;
                    i++;
                    continue;
                }

                if (argv[i].equals("-Ec")) {
                    execMode = Mode.CACHED;
                    i++;
                    continue;
                }

                if (argv[i].equals("-Ef")) {
                    execMode = Mode.FIXED;
                    i++;
                    continue;
                }

               /* Handle positional parameters */
                fname = argv[i];
                pattern = argv[i + 1].toCharArray();
                i += 2;

                if (argv.length > i) {
                    ntasks = Integer.valueOf(argv[i]);
                    i++;
                }

                if (argv.length > i) {
                    nthreads = Integer.valueOf(argv[i]);
                    i++;
                }

                if (argv.length > i)
                    throw new Exception("Too many arguments");
            }

            /* Read file into memory */
            InputStreamReader file = new InputStreamReader(new FileInputStream(fname));
            
            Arrays.fill(text, '.');
            len = file.read(text);

            if (file.read() >= 0)
                System.out.println("\nWarning: file truncated to " + max + " characters\n");

            if (ntasks <= 0 || nthreads <= 0 || pattern.length <= 0 || warmups <0 || runs <= 0)
                throw new Exception("Illegal argument(s)");

        } catch (Exception e) {
            System.out.print(e + "\n\nUsage:   java Search <options> file pattern [ntasks [nthreads]] \n\n"
                               + "  where: 0 < nthreads, 0 < ntasks, 0 < size(pattern)\n" + "  Options: \n"
                               + "    -P           Print found positions\n"
                               + "    -W w         Make w warmup searches (w >=0)\n"
                               + "    -R r         Run the search n times (r > 0)\n"
                               + "    -d datafile  Define datafile\n" 
                               + "    -Es          Single-threaded executor\n"
                               + "    -Ec          Cached multi-threaded executor\n"
                               + "    -Ef          Fixed-size thread executor\n"
                               + "\n");
            System.exit(1);
        }
    }

    static void writeResult(List<Integer> res) {
        System.out.print("" + res.size() + " occurrences found in ");
        if (printPos) {
            int i = 0;
            System.out.println();
            for (int pos : res) {
                System.out.printf(" %6d", pos);
                if (++i % 10 == 0)
                    System.out.println();
            }
            System.out.println();
        }
    }

    static void writeTime(double time) {
        System.out.printf("%1.6f s", time);
    }

    static void writeRun(int no) {
        System.out.printf("Run no. %2d: ", no);
    }

    static void writeData(String s) {
        try {
            if (datafile != null) {
                // Append result to data file
                FileWriter f = new FileWriter(datafile,true);
                PrintWriter data =  new PrintWriter(new BufferedWriter(f));
                data.println(s);
                data.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static ExecutorService getEngine(){
        return execMode == Mode.SINGLE ? Executors.newSingleThreadExecutor()   :
               execMode == Mode.CACHED ? Executors.newCachedThreadPool() :
               /* Mode.FIXED */ Executors.newFixedThreadPool(nthreads);
    }

    //Warmup put into functions.
    static void runWarmup(int warmups, SearchTask singleSearch){
        /* Setup selected execution engine */
        for(int i = 0; i < warmups; i++){
            try{
                getEngine().submit(singleSearch).get();
            } catch (Exception e) {
                System.out.println("Search: " + e);
            }
        }
    } 

    static void runWarmup(int warmups, List<SearchTask> taskList){
        /* Setup selected execution engine */
        for(int i = 0; i < warmups; i++){
            try{
                getEngine().invokeAll(taskList);
            } catch (Exception e) {
                System.out.println("Search: " + e);
            }
        }
    }

    // class ExecutionHandler implements Runnable{
    //     private ExecutorService engine;
    //     private List<SearchTask> taskList;
    //     private SearchTask task;

    //     public ExecutionHandler(ExecutorService engine, SearchTask task){
    //         this.engine = engine;
    //         this.task = task;
    //     }

    //     public ExecutionHandler(ExecutorService engine, List<SearchTask> taskList){
    //         this.engine = engine;
    //         this.taskList = taskList;
    //     }
    // }

    public static void main(String[] argv) {
        try {
            long start;
            double time, totalTime = 0.0;
            
            /* Get and print program parameters */
            getArguments(argv);
            System.out.printf("\nFile=%s, length=%d, pattern='%s'\nntasks=%d, nthreads=%d, warmups=%d, runs=%d\nexecutor: %s\n" , 
                    fname, len, new String(pattern), ntasks, nthreads, warmups, runs, execMode.toString());

            

            /**********************************************
             * Run search using a single task
             *********************************************/
            SearchTask singleSearch = new SearchTask(text, pattern, 0, len);

            List<Integer> singleResult = null;

            /*
             * Run a couple of times on engine for loading all classes and
             * cache warm-up
             */
            runWarmup(warmups, singleSearch);
            
            /* Run for time measurement(s) and proper result */
            totalTime = 0.0;
            
            for (int run = 0; run < runs; run++) {
                start = System.nanoTime();

                singleResult = getEngine().submit(singleSearch).get();

                time = (double) (System.nanoTime() - start) / 1e9;
                totalTime += time;    
                
                System.out.print("\nSingle task: ");
                writeRun(run);  writeResult(singleResult);  writeTime(time);  
            }
            
            double singleTime = totalTime / runs;
            System.out.print("\n\nSingle task (avg.): "); 
            writeTime(singleTime);  System.out.println();

                        
            /**********************************************
             * Run search using multiple tasks
             *********************************************/
         
            // Create list of tasks
            List<SearchTask> taskList = new ArrayList<SearchTask>();

            for (int i = 0; i < ntasks; i++) {
                int from = i == 0 ? i * (len / ntasks) : i * (len / ntasks) - (pattern.length - 1);
                int to = (i + 1) * len / ntasks;
                taskList.add(new SearchTask(text, pattern, from, to));
            }

            // TODO: Add tasks to list here

            List<Integer> result = null;
            
            // Run the tasks a couple of times
            runWarmup(warmups, taskList);
            
            totalTime = 0.0;
            
            for (int run = 0; run < runs; run++) {

                start = System.nanoTime();

                // Submit tasks and await results
                List<Future<List<Integer>>> futures = getEngine().invokeAll(taskList);

                // Overall result is an ordered list of unique occurrence positions
                result = new LinkedList<Integer>();

                for (Future<List<Integer>> future : futures) {
                    for (int pos : future.get()) {
                        if (!result.contains(pos))
                            result.add(pos);
                    }
                }
                
                // TODO: Combine future results into an overall result 
                time = (double) (System.nanoTime() - start) / 1e9;
                totalTime += time;    
                
                System.out.printf("\nUsing %2d tasks: ", ntasks);
                writeRun(run);  writeResult(result);  writeTime(time);
            }

            double multiTime = totalTime / runs;
            System.out.printf("\n\nUsing %2d tasks (avg.): ", ntasks); 
            writeTime(multiTime);  System.out.println();

            
            if (!singleResult.equals(result)) {
                System.out.println("\nERROR: lists differ");
            }
            System.out.printf("\n\nAverage speedup: %1.2f\n\n", singleTime / multiTime);

            
            /**********************************************
             * Terminate engine after use
             *********************************************/
            getEngine().shutdown();

        } catch (Exception e) {
            System.out.println("Search: " + e);
        }
    }
}
