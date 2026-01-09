package lld.designpattren.logger;

public class ConsoleLogger implements Logger {

    private static ConsoleLogger instance;

    private ConsoleLogger() {
    }

    public static ConsoleLogger getInstance() {
        if (instance == null) {
            synchronized (ConsoleLogger.class) {
                try {
                    Thread.sleep(10);   // SIMULATE delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (instance == null) {  // Second check (with lock)
                    instance = new ConsoleLogger();
                }
            }

        }
        return instance;
    }

    @Override
    public void log(String message) {
        System.out.println("[LOG] " + message);
    }
}