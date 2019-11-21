package org.home.streaming.events;

public class DataPoint<T>
{
	private long timestamp;
	private T value;

	public DataPoint() {}

	public DataPoint(long timestamp, T value)
	{
		this.timestamp = timestamp;
		this.value = value;
	}

	public DataPoint(long timestamp)
	{
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

	public <K> DataPoint<K> withNewValue(K newValue)
	{
		return new DataPoint<K>(this.timestamp, newValue);
	}
}
