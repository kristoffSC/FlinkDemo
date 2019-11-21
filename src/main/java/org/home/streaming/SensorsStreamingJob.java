/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.home.streaming;

import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.home.streaming.events.DataPoint;
import org.home.streaming.events.KeyedDataPoint;
import org.home.streaming.operators.AssignKeyFunction;
import org.home.streaming.operators.SawtoothFunction;
import org.home.streaming.operators.SineWaveFunction;
import org.home.streaming.operators.SquareWaveFunction;
import org.home.streaming.sources.TimestampSource;

/**
 * Skeleton for a Flink Streaming Job.
 *
 * <p>For a tutorial how to write a Flink streaming application, check the
 * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
 *
 * <p>To package your application into a JAR file for execution, run
 * 'mvn clean package' on the command line.
 *
 * <p>If you change the name of the main class (with the public static void main(String[] args))
 * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
 */
public class SensorsStreamingJob
{

	public static void main(String[] args) throws Exception
	{
		// set up the streaming execution environment
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		DataStream<KeyedDataPoint<Double>> sensorStream = generateSensorData(env);

		sensorStream.print();

		env.execute("Flink Streaming Java API Skeleton");
	}

	private static DataStream<KeyedDataPoint<Double>> generateSensorData(StreamExecutionEnvironment env)
	{

		env.setRestartStrategy(RestartStrategies.fixedDelayRestart(1000, 1000));
		env.setParallelism(1);
		env.disableOperatorChaining();

		final int SLOWDOWN_FACTOR = 1;
		final int PERDIOD_MS = 100;

		// Initial data just timestamped message
		DataStreamSource<DataPoint<Long>> timestampSource =
				env.addSource(new TimestampSource(PERDIOD_MS, SLOWDOWN_FACTOR), "test data");

		SingleOutputStreamOperator<DataPoint<Double>> sawtoothStream = timestampSource
				.map(new SawtoothFunction(10))
				.name("sawTooth");

		SingleOutputStreamOperator<KeyedDataPoint<Double>> tempStream = sawtoothStream
				.map(new AssignKeyFunction("temp"))
				.name("assignedKey(temp)");

		SingleOutputStreamOperator<KeyedDataPoint<Double>> pressureStream = sawtoothStream
				.map(new SineWaveFunction())
				.name("sineWave")
				.map(new AssignKeyFunction("pressure"))
				.name("assignKey(pressure");


		SingleOutputStreamOperator<KeyedDataPoint<Double>> doorStream = sawtoothStream
				.map(new SquareWaveFunction())
				.name("square")
				.map(new AssignKeyFunction("door"))
				.name("assignKey(door)");


		DataStream<KeyedDataPoint<Double>> sensorStream = tempStream
				.union(pressureStream)
				.union(doorStream);

		return sensorStream;

	}
}
