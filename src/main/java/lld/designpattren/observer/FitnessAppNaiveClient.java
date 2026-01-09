package lld.designpattren.observer;

class FitnessDataNaiveClient {
    private int steps;
    private int activeMinutes;
    private int calories;

    // Direct, hardcoded references to all dependent modules
   // private LiveActivityDisplayNaive liveDisplay = new LiveActivityDisplayNaive();
    private ProgressLoggerNaive progressLogger = new ProgressLoggerNaive();
    private NotificationServiceNaive notificationService = new NotificationServiceNaive();

    public void newFitnessDataPushed(int newSteps, int newActiveMinutes, int newCalories) {
        this.steps = newSteps;
        this.activeMinutes = newActiveMinutes;
        this.calories = newCalories;

        System.out.println("\nFitnessDataNaive: New data received - Steps: " + steps +
                ", ActiveMins: " + activeMinutes + ", Calories: " + calories);

        // Manually notify each dependent module
       // liveDisplay.showStats(steps, activeMinutes, calories);
        progressLogger.logDataPoint(steps, activeMinutes, calories);
        notificationService.checkAndNotify(steps);
    }

    public void dailyReset() {
        // Reset logic...
        if (notificationService != null) {
            notificationService.resetDailyNotifications();
        }
        System.out.println("FitnessDataNaive: Daily data reset.");
        newFitnessDataPushed(0, 0, 0); // Notify with reset state
    }
}
