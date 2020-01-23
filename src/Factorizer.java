import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;

public class Factorizer implements Runnable {

    private static final BigInteger MIN = BigInteger.valueOf(1);
    private BigInteger min;
    private BigInteger max;
    private int step;
    private BigInteger product;

    public Factorizer(BigInteger product, int step){
        this.product = product;
        this.step = step;
    }

    public void run() {
       BigInteger number = min;
        while (number.compareTo(max) <= 0) {
            if (product.remainder(number).compareTo(BigInteger.ZERO) == 0) {
                factor1 = number;
                factor2 = product.divide(factor1);
                return;
            }
            number = number.add(BigInteger.valueOf(step));
        }
    }

    public static void main(String[] args){
        ArrayList<BigInteger> prinmes = new ArrayList<>();
        try{
            //Get input from user
            InputStreamReader streamreader = new InputStreamReader((System.in));
            BufferedReader consoleReader = new BufferedReader(streamreader);
            System.out.print("Please enter an interfer taht is a factor of two prime numbers: ");
            String input; //= consoleReader.readLine();
            BigInteger product = new BigInteger(consoleReader.readLine());
            System.out.print(("Number of threads to be used: "));
            int numTreads = Integer.parseInt(consoleReader.readLine());

            long start = System.nanoTime();
            Thread[] threads = new  Thread[numTreads];
            Factorizer[] factorizers = new Factorizer[numTreads];

            for (int x = 0; x < numTreads; x++){
                factorizers[x] = new Factorizer(product,numTreads);
                threads[x] = new Thread(factorizers[x]);
            }

            for(Thread t : threads){
                t.start();
            }

            long stop = System.nanoTime();

        }catch (Exception exception){

        }

    }
}
