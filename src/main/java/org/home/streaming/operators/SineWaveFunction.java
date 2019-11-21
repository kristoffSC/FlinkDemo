package org.home.streaming.operators;

import org.apache.flink.api.common.functions.RichMapFunction;
import org.home.streaming.events.DataPoint;

public class SineWaveFunction extends RichMapFunction<DataPoint<Double>, DataPoint<Double>>
{

	@Override public DataPoint<Double> map(DataPoint<Double> doubleDataPoint) throws Exception
	{
		double phase = doubleDataPoint.getValue() * 2 * Math.PI;
		return doubleDataPoint.withNewValue(Math.sin(phase));
	}
}
