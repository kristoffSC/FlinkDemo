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

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;
import org.home.streaming.events.DataPoint;
import org.home.streaming.events.KeyedDataPoint;
import org.home.streaming.events.SocketEvent;
import org.home.streaming.operators.*;
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
public class SensorsStreamingJob {

    public static void main(String[] args) throws Exception {
        // set up the streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        /////////////
        DataStream<KeyedDataPoint> sensorStream = generateSensorData(env);
        sensorStream
                .addSink(new InfluxDbSink<>("sensors"))
                .name("Sensors Sink");

        KeyedStream<KeyedDataPoint, String> keyedSensorStream = sensorStream
                .keyBy((KeySelector<KeyedDataPoint, String>) value -> value.key);

        keyedSensorStream
                .timeWindow(Time.seconds(1))
                .sum("value")
                .addSink(new InfluxDbSink<>("summedSensors"))
                .name("Summed Sensors Sink");

        ///////////////////

        SingleOutputStreamOperator<SocketEvent> socketStream = env.socketTextStream("localhost", 9090)
                .flatMap(new FlatMapFunction<String, SocketEvent>() {
                    @Override
                    public void flatMap(String value, Collector<SocketEvent> out) throws Exception {
                        String[] tokens = value.split(" ");

                        if (tokens.length == 2) {
                            SocketEvent record = new SocketEvent(tokens[0], Double.valueOf(tokens[1]));
                            System.out.println(record);
                            out.collect(record);
                        }
                    }
                })
                .name("socketEventStream");


        MapStateDescriptor<String, SocketEvent> ruleStateDescriptor = new MapStateDescriptor<>(
                "RulesBroadcastState",
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(new TypeHint<SocketEvent>() {
                }));

        BroadcastStream<SocketEvent> socketBroadcastStream = socketStream
                .broadcast(ruleStateDescriptor);

        keyedSensorStream
                .connect(socketBroadcastStream)
                .process(new AmplifierFunction())
                .addSink(new InfluxDbSink<>("amplifiedSensors"))
                .name("Amplified Sensors Sink");


        /////////////////

        //sensorStream.print();
        env.execute("Flink Streaming Java API Skeleton");
    }

    private static DataStream<KeyedDataPoint> generateSensorData(StreamExecutionEnvironment env) {

        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(1000, 1000));
        env.setParallelism(1);
        env.disableOperatorChaining();

        final int SLOWDOWN_FACTOR = 1;
        final int PERDIOD_MS = 10;

        // Initial data just timestamped message
        DataStreamSource<DataPoint> timestampSource =
                env.addSource(new TimestampSource(PERDIOD_MS, SLOWDOWN_FACTOR), "test data");

        SingleOutputStreamOperator<DataPoint> sawtoothStream = timestampSource
                .map(new SawtoothFunction(100))
                .name("sawTooth");

        SingleOutputStreamOperator<KeyedDataPoint> tempStream = sawtoothStream
                .map(new AssignKeyToDataPointFunction("temp"))
                .name("assignedKey(temp)");

        SingleOutputStreamOperator<KeyedDataPoint> pressureStream = sawtoothStream
                .map(new SineWaveFunction())
                .name("sineWave")
                .map(new AssignKeyToDataPointFunction("pressure"))
                .name("assignKey(pressure");


        SingleOutputStreamOperator<KeyedDataPoint> doorStream = sawtoothStream
                .map(new SquareWaveFunction())
                .name("square")
                .map(new AssignKeyToDataPointFunction("door"))
                .name("assignKey(door)");


        DataStream<KeyedDataPoint> sensorStream =
                tempStream
                        .union(pressureStream)
                        .union(doorStream);

        return sensorStream;

    }
}
