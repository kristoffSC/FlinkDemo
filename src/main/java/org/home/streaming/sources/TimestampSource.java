package org.home.streaming.sources;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.home.streaming.events.DataPoint;

import java.util.Collections;
import java.util.List;

public class TimestampSource extends RichSourceFunction<DataPoint> implements ListCheckpointed<Long> {

    private final int periodMs;
    private final int slowdownFactor;

    private volatile boolean running = true;
    private volatile long currentTimeMs = 0;

    public TimestampSource(int periodMs, int slowdownFactor) {
        this.periodMs = periodMs;
        this.slowdownFactor = slowdownFactor;
    }

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        long now = System.currentTimeMillis();
        if (currentTimeMs == 0) {
            currentTimeMs = now - (now % 1000); //floor to second boundary;
        }
        timeSync();
    }

    @Override
    public List<Long> snapshotState(long checkpointId, long checkpointTimestamp) throws Exception {
        return Collections.singletonList(currentTimeMs);
    }

    @Override
    public void restoreState(List<Long> list) throws Exception {
        currentTimeMs = list.get(0);
    }

    @Override
    public void run(SourceContext<DataPoint> sourceContext) throws Exception {
        while (running) {
            synchronized (sourceContext.getCheckpointLock()) {
                sourceContext.collectWithTimestamp(new DataPoint(currentTimeMs, 0L), currentTimeMs);
                sourceContext.emitWatermark(new Watermark(currentTimeMs));
                currentTimeMs += periodMs;
            }
            timeSync();
        }
    }

    private void timeSync() throws InterruptedException {
        long realTimeDeltaMs = currentTimeMs - System.currentTimeMillis();
        long sleepTime = periodMs + realTimeDeltaMs;//+ randomJitter();


        if (slowdownFactor != 1) {
            sleepTime = periodMs * slowdownFactor;
        }

        if (sleepTime > 0) {
            Thread.sleep(sleepTime);
        }
    }

    @Override
    public void cancel() {
        running = false;
    }


}
