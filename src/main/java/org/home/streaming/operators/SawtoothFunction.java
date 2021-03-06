package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.home.streaming.events.DataPoint;

import java.util.Collections;
import java.util.List;

public class SawtoothFunction extends RichMapFunction<DataPoint, DataPoint> implements ListCheckpointed<Long> {
    final private int numSteps;

    // State
    private long currentSep;

    public SawtoothFunction(int numSteps) {
        this.numSteps = numSteps;
    }

    @Override
    public DataPoint map(DataPoint dataPoint) throws Exception {
        double phase = (double) currentSep / numSteps;
        currentSep = ++currentSep % numSteps;
        return new DataPoint(dataPoint.timestamp, phase);
    }

    @Override
    public List<Long> snapshotState(long l, long l1) throws Exception {
        return Collections.singletonList(currentSep);
    }

    @Override
    public void restoreState(List<Long> list) throws Exception {
        currentSep = list.get(0);
    }
}
