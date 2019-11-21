package org.home.streaming.events;

public class KeyedDataPoint<T>
{
	private T value;
	private String key;
	private long timestamp;

	public KeyedDataPoint()
	{

	}

	public KeyedDataPoint(T value, String key, long timestamp)
	{
		this.value = value;
		this.key = key;
		this.timestamp = timestamp;
	}

	public T getValue()
	{
		return value;
	}

	public void setValue(T value)
	{
		this.value = value;
	}

	public long getTimestamp()
	{
		return timestamp;
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
