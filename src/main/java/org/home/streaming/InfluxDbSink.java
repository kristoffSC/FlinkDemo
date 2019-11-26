package org.home.streaming;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.home.streaming.events.DataPoint;
import org.home.streaming.events.KeyedDataPoint;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.util.concurrent.TimeUnit;

public class InfluxDbSink<T extends DataPoint<? extends Number>> extends RichSinkFunction<T>
{

	private static final String databaseName = "sineWave";
	private static final String fieldName = "value";

	private final String measurement;
	private transient InfluxDB influxDb;

	public InfluxDbSink(String measurement)
	{
		this.measurement = measurement;
	}

	@Override
	public void open(Configuration parameters) throws Exception
	{
		super.open(parameters);

		influxDb = InfluxDBFactory.connect("http://localhost:8086", "admin", "admin");
		influxDb.query(new Query("CREATE DATABASE " + databaseName));
		influxDb.setDatabase(databaseName);
		influxDb.enableBatch(2000, 100, TimeUnit.MILLISECONDS);
	}

	@Override
	public void invoke(DataPoint dataPoint, Context context) throws Exception
	{
		Point.Builder builder = Point.measurement(measurement).time(dataPoint.getTimestamp(), TimeUnit.MILLISECONDS)
				.addField(fieldName, dataPoint.getValue());

		if (dataPoint instanceof KeyedDataPoint) {
			builder.tag("key", ((KeyedDataPoint) dataPoint).getKey());
		}

		Point point = builder.build();
		influxDb.write(databaseName, "", point);

	}
}
