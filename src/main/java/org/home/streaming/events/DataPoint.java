package org.home.streaming.events;

public class DataPoint
{
	public long timestamp;
	public double value;

	public DataPoint() {}

	public DataPoint(long timestamp, double value)
	{
		this.timestamp = timestamp;
		this.value = value;
	}

	@Override
	public String toString() {
		return "DataPoint{" +
				"timestamp=" + timestamp +
				", value=" + value +
				'}';
	}
}
