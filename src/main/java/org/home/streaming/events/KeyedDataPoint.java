package org.home.streaming.events;

public class KeyedDataPoint<T extends Number> extends DataPoint<T>
{
	private String key;

	public KeyedDataPoint(T value, String key, long timestamp)
	{
		super(timestamp, value);
		this.key = key;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}
}
