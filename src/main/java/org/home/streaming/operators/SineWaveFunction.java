package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;

public class SineWaveFunction extends RichMapFunction<DataPoint, DataPoint>
{

	@Override public DataPoint map(DataPoint doubleDataPoint) throws Exception
	{
		double phase = doubleDataPoint.value * 2 * Math.PI;
		return new DataPoint(doubleDataPoint.timestamp, Math.sin(phase));
	}
}
