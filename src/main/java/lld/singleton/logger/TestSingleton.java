package lld.singleton.logger;

public class TestSingleton {
    public static void main(String[] args) {

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                ConsoleLogger logger = ConsoleLogger.getInstance();
                System.out.println(logger.hashCode());
            });

            t.start();
        }
    }
}