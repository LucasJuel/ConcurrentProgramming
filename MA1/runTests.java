import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.io.*;
import java.lang.Math;

class runTests{
    static void writeData(String s) {
        String datafile = "tempData.txt";
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

    public static void main(String[] args) {
        //Test for problem 1
        //String[] arg = {"-R", "10","-W","2", "-d", "data.txt", "wikipedia.txt","World Association of Theoretical and Computational Chemists"};
        //Search.main(arg);
/*
        //Test for problem 2
        writeData("Problem 2");
        for(int i = 5; i <= 30; i+=5){
            String[] arg = {"-R", "10", "-W", "2", "-d", "tempData.txt", "wikipedia.txt", "World Association of Theoretical and Computational Chemists", Integer.toString(i)};
            Search.main(arg);
        }

        //Test for Problem 3
        writeData("Problem 3.1");
        for(int i = 1; i <= 20; i++){
            String[] arg = {"-R", "10", "-W", "2", "-d", "tempData.txt", "-Ec", "wikipedia.txt", "World Association of Theoretical and Computational Chemists", Integer.toString(i)};
            Search.main(arg);
        }
       
        writeData("Problem 3.2");
        for(int i = 500; i <= 10000; i+=500){
            String[] arg = {"-R", "10", "-W", "2", "-d", "tempData.txt", "-Ec", "wikipedia.txt", "World Association of Theoretical and Computational Chemists", Integer.toString(i)};
            Search.main(arg);
        }
*/         
        //Test for Problem 4
         writeData("Problem 4");
         for(int i = 2; i <= 10; i+=2){
             writeData("Threads: " + i);
             for(int j = 1; j <= 20; j++){
                 String[] arg = {"-R", "10", "-W", "2", "-d", "tempData.txt", "-Ef", "wikipedia.txt", "World Association of Theoretical and Computational Chemists", Integer.toString(j), Integer.toString(i)};
                 Search.main(arg);
             }
         }
    }
}