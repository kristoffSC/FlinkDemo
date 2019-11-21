package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;
import org.home.streaming.events.KeyedDataPoint;

public class AssignKeyFunction extends RichMapFunction<DataPoint<Double>, KeyedDataPoint<Double>>
{
	private final String assignedKey;

	public AssignKeyFunction(String assignedKey)
	{

		this.assignedKey = assignedKey;
	}

	@Override public KeyedDataPoint<Double> map(DataPoint<Double> doubleDataPoint) throws Exception
	{
		try {
			return new KeyedDataPoint<>(doubleDataPoint.getValue(), assignedKey, doubleDataPoint.getTimestamp());
		} catch (Exception e) {
			throw e;
		}

	}
}
