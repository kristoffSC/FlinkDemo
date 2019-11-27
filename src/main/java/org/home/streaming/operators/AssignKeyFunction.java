package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;
import org.home.streaming.events.KeyedDataPoint;

public class AssignKeyFunction extends RichMapFunction<DataPoint, KeyedDataPoint> {
    private final String assignedKey;

    public AssignKeyFunction(String assignedKey) {

        this.assignedKey = assignedKey;
    }

    @Override
    public KeyedDataPoint map(DataPoint doubleDataPoint) throws Exception {
        return new KeyedDataPoint(doubleDataPoint.value, assignedKey, doubleDataPoint.timestamp);
    }
}
