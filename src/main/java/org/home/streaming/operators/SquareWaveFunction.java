package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;

import javax.xml.crypto.Data;

public class SquareWaveFunction extends RichMapFunction<DataPoint<Double>, DataPoint<Double>>
{

	@Override public DataPoint<Double> map(DataPoint<Double> doubleDataPoint) throws Exception
	{
		return new DataPoint<>(doubleDataPoint.getTimestamp(), (double) Math.round(doubleDataPoint.getValue()));
	}
}
