package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;

public class SquareWaveFunction extends RichMapFunction<DataPoint, DataPoint> {

    @Override
    public DataPoint map(DataPoint doubleDataPoint) throws Exception {
        return new DataPoint(doubleDataPoint.timestamp, Math.round(doubleDataPoint.value));
    }
}
