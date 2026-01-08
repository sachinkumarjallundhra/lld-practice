package lld.machinecoding.trainplatformmanagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainPlatformManager {

    static class Interval {
        String trainId;
        int start;
        int end;

        Interval(String trainId, int start, int end) {
            this.trainId = trainId;
            this.start = start;
            this.end = end;
        }
    }

    static class Platform {
        int index;
        List<Interval> schedule = new ArrayList<>();

        Platform(int index) {
            this.index = index;
        }
    }

    static class TrainRecord {
        String trainId;
        int platform;
        int start;
        int end;

        TrainRecord(String trainId, int platform, int start, int end) {
            this.trainId = trainId;
            this.platform = platform;
            this.start = start;
            this.end = end;
        }
    }

    private Platform[] platforms;
    private Map<String, TrainRecord> trainMap = new HashMap<>();

    public TrainPlatformManager(int platformCount) {

        platforms = new Platform[platformCount];
        for (int i = 0; i < platformCount; i++) {
            platforms[i] = new Platform(i);
        }

    }

    public String assignPlatform(String trainId, int arrivalTime, int waitTime) {
        int bestPlatform = -1;
        int bestDelay = Integer.MAX_VALUE;
        int bestStart = -1;

        for (Platform p : platforms) {

            int start = earliestStart(p, arrivalTime, waitTime);
            int delay = start - arrivalTime;

            if (delay < bestDelay ||
                    (delay == bestDelay && p.index < bestPlatform)) {

                bestDelay = delay;
                bestPlatform = p.index;
                bestStart = start;
            }
        }

        int end = bestStart + waitTime - 1;

        Interval interval = new Interval(trainId, bestStart, end);
        platforms[bestPlatform].schedule.add(interval);

        // keep schedule sorted
        platforms[bestPlatform].schedule.sort(
                (a, b) -> Integer.compare(a.start, b.start)
        );

        trainMap.put(trainId,
                new TrainRecord(trainId, bestPlatform, bestStart, end));

        return bestPlatform + "," + bestDelay;

    }

    public String getTrainAtPlatform(int platformNumber, int timestamp) {
        Platform p = platforms[platformNumber];

        for (Interval in : p.schedule) {
            if (in.start <= timestamp && timestamp <= in.end) {
                return in.trainId;
            }
        }
        return "";
    }

    public int getPlatformOfTrain(String trainId, int timestamp) {
        TrainRecord tr = trainMap.get(trainId);
        if (tr == null) return -1;

        if (tr.start <= timestamp && timestamp <= tr.end) {
            return tr.platform;
        }
        return -1;
    }

    private int earliestStart(Platform p, int arrivalTime, int waitTime) {

        int start = arrivalTime;

        for (Interval in : p.schedule) {
            int end = start + waitTime - 1;

            // Fits before this interval
            if (end < in.start) {
                return start;
            }

            // Overlaps → push start to after this interval
            if (start <= in.end) {
                start = in.end + 1;
            }
        }

        // Fits after last interval
        return start;
    }
}
