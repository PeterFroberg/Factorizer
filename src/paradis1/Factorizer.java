package paradis1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.concurrent.locks.ReentrantLock;

public class Factorizer implements Runnable {

    private static class WorkStatus {
        private boolean completed = false;
        private ReentrantLock lock = new ReentrantLock();

        private boolean isCompleted() {
            if (lock.tryLock()) {
                try {
                    return completed;
                } finally {
                    lock.unlock();
                }
            }
            return false;
        }

        private void markCompleted(BigInteger factor1, BigInteger factor2) {
            lock.lock();
            try {
                if (!completed) {
                    this.completed = true;
                    System.out.println("Factor 1: " + factor1 + " Factor 2: " + factor2);
                }
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }

        }
    }

    private BigInteger max, step, product, threadStartValue;
    private final WorkStatus workStatus;

    private Factorizer(BigInteger product, BigInteger step, BigInteger threadStartValue, WorkStatus workStatus, BigInteger max) {
        this.product = product;
        this.step = step;
        this.threadStartValue = threadStartValue;
        this.workStatus = workStatus;
        this.max = max;
    }

    public void run() {

        BigInteger number = threadStartValue;

        while (number.compareTo(max) < 0 && !workStatus.isCompleted()) {
            if (product.remainder(number).compareTo(BigInteger.ZERO) == 0 && isPrime(number)) {
                if (workStatus.isCompleted()) {
                    return;
                }
                workStatus.markCompleted(number, product.divide(number));
                return;
            }
            number = number.add(step);
        }//End while
    }

    private boolean isPrime(BigInteger number) {
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
            consoleReader.close();

            //create threads
            long start = System.nanoTime();
            Thread[] threads = new Thread[numThreads];
            Factorizer[] factorizers = new Factorizer[numThreads];
            WorkStatus workStatus = new WorkStatus();

            BigInteger max = product.sqrt();

            for (int x = 0; x < numThreads; x++) {
                factorizers[x] = new Factorizer(product, BigInteger.valueOf(numThreads), BigInteger.TWO.add(BigInteger.valueOf(x)), workStatus, max);
                threads[x] = new Thread(factorizers[x]);
            }

            //Start threads, Joins threads
            for (Thread t : threads) {
                t.start();
            }

            for (int x = 0; x < numThreads; x++) {
                threads[x].join();
            }

            if (!workStatus.isCompleted()) {
                System.out.println("No factorization possible");
            }
            
            long stop = System.nanoTime();
            System.out.println("\nExecution time (milliseconds): " + (stop - start) / 1000000000.0 + "seconds");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
