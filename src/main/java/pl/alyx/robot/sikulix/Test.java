package pl.alyx.robot.sikulix;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.Scanner;

public class Test implements Runnable {

    public static class Runner implements Runnable {

        private final Integer number;

        public Runner(Integer number) {
            this.number = number;
        }

        @Override
        public void run() {
            Random r = new Random();
            for (int i = 0; i < 10; i++) {
                System.out.printf("%s Thread %d (%d): Value=%d%n",
                        DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now()),
                        this.number,
                        Thread.currentThread().getId(),
                        Single.INSTANCE.increment()
                );
                long delay = r.nextInt(500);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void run() {

        for (int i = 1; i <= 10; i++) {
            Runner r = new Runner(i);
            Thread t = new Thread(r);
            System.out.printf("%s Starting thread %d%n",
                    DateTimeFormatter.ofPattern("HH:mm:ss.SSS").format(LocalDateTime.now()),
                    i
            );
            t.start();
        }

        try (Scanner s = new Scanner(System.in)) {
            s.next();
        }

    }

}
