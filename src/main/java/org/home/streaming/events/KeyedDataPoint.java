package org.home.streaming.events;

public class KeyedDataPoint extends DataPoint
{
	public String key;

	public KeyedDataPoint() {}

	public KeyedDataPoint(double value, String key, long timestamp)
	{
		super(timestamp, value);
		this.key = key;
	}
}
