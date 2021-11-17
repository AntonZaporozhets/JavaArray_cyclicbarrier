import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;


class Mass implements Runnable{
    private final int[] arr;
    CyclicBarrier cyclbar;
    private long sum;

    Mass(CyclicBarrier c, int[] a) {
        this.arr = a;
        cyclbar = c;
    }

    public void run() {
        for (int j : arr) {
            sum += j;
            //System.out.printf("%s: %s \n", Thread.currentThread().getName(), sum);
        }
        try {
            cyclbar.await();
        } catch (BrokenBarrierException | InterruptedException exc) {
            System.out.println(exc);
        }

    }

    public long getSum() {
        return sum;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Input a size of array: ");
        int size = in.nextInt();
        int[] array = new int[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = (int) Math.round((Math.random()*200 - 100));
        }

        System.out.print("Input a number of threads: ");
        int numThreads = in.nextInt();
        int[] modArray = Arrays.copyOfRange(array, size-(size % numThreads), size);
        long modSum = 0;
        for (int j : modArray) {
            modSum += j;
        }

        List<Mass> threads = new ArrayList<>();
        long finalModSum = modSum;
        CyclicBarrier cb = new CyclicBarrier(numThreads, new Runnable() {
            @Override
            public void run() {
                long res = 0;
                for (Mass thr : threads) {
                    res += thr.getSum();
                }
                System.out.println(res+finalModSum);
            }
        });
        for (int i = 0; i < numThreads; i++) {
            threads.add(new Mass(cb, Arrays.copyOfRange(array, i*(size/numThreads), (i+1)*(size/numThreads))));
        }

        for (Mass thr : threads) {
            new Thread(thr).start();
        }

    }
}