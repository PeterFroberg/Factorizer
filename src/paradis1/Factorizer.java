package paradis1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class Factorizer implements Runnable {

    private static class WorkStatus {
        private boolean completed = false;
        private Object lock = new Object();

        public boolean isCompleted() {
            synchronized (lock) {
                return completed;
            }
        }

        public void markCompleted(boolean completed) {
            synchronized (lock) {
                this.completed = completed;
            }
        }
    }

    private BigInteger max;
    private BigInteger step;
    private BigInteger product;
    private BigInteger threadStartValue;
    private WorkStatus workStatus;// = new WorkStatus();


    public Factorizer(BigInteger product, BigInteger step, BigInteger threadStartValue, WorkStatus workStatus, BigInteger max) {
    //public Factorizer(BigInteger product, BigInteger step, BigInteger threadStartValue, BigInteger max) {
        this.product = product;
        this.step = step;
        this.threadStartValue = threadStartValue;
        this.workStatus = workStatus;
        this.max = max;
    }

    public void run() {
        if (isPrime(product)) {
            if(!workStatus.isCompleted()) {
                workStatus.markCompleted(true);
                System.out.println("No factorization possible");
            }
            //return;
        }
        BigInteger factor1, factor2;

        BigInteger number = threadStartValue;

        while (number.compareTo(max) <= 0 && !workStatus.isCompleted()) {

            if (product.remainder(number).compareTo(BigInteger.ZERO) == 0) {
                if (workStatus.isCompleted()) {
                    return;
                } //end if iscomplete

                if (isPrime(number)) {
                    factor1 = number;
                    factor2 = product.divide(factor1);
                    //synchronized (workStatus) {
                        if (workStatus.isCompleted()) {
                            return;
                        }
                        workStatus.markCompleted(true);
                        System.out.println("Factor 1: " + factor1 + " Factor 2: " + factor2);
                    //}
                }
            }
            number = number.add(step);
        }//End while


    }



    public boolean isPrime(BigInteger number) {
        boolean result = true;
        for (BigInteger d = new BigInteger("2"); d.compareTo(number.sqrt()) <= 0; d = d.add(BigInteger.ONE)) {
            if (number.remainder(d).equals(BigInteger.ZERO)) {
                result = false;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        try {
            //Get input from user
            InputStreamReader streamreader = new InputStreamReader((System.in));
            BufferedReader consoleReader = new BufferedReader(streamreader);
            System.out.print("Please enter a factor of two prime numbers: ");
            String input; //= consoleReader.readLine();
            BigInteger product = new BigInteger(consoleReader.readLine());
            System.out.print(("Number of threads to be used: "));
            int numThreads = Integer.parseInt(consoleReader.readLine());

            long start = System.nanoTime();
            Thread[] threads = new Thread[numThreads];
            Factorizer[] factorizers = new Factorizer[numThreads];
            WorkStatus workStatus = new WorkStatus();

            BigInteger max = product.sqrt();

            for (int x = 0; x < numThreads; x++) {
                factorizers[x] = new Factorizer(product, BigInteger.valueOf(numThreads), BigInteger.TWO.add(BigInteger.valueOf(x)), workStatus, max);
                //factorizers[x] = new Factorizer(product, BigInteger.valueOf(numThreads), BigInteger.TWO.add(BigInteger.valueOf(x)), max);
                threads[x] = new Thread(factorizers[x]);
            }

            for (Thread t : threads) {
                t.start();
            }

            for (int i = 0; i < numThreads; i++) {
                threads[i].join();
            }

            long stop = System.nanoTime();

            System.out.println("\nExecution time (milliseconds): " + (stop - start) / 1000000000.0 + "seconds");

        } catch (Exception exception) {

        }

    }
}
