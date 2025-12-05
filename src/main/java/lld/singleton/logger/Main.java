package lld.singleton.logger;

public class Main {
    public static void main(String[] args) {

        Logger logger1 = ConsoleLogger.getInstance();
        Logger logger2 = ConsoleLogger.getInstance();

        logger1.log("Application started");
        logger2.log("This should be the same instance");

        System.out.println("Are both loggers same? " + (logger1 == logger2));
    }
}