package org.home.streaming.operators;

import org.apache.flink.api.common.state.BroadcastState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.co.KeyedBroadcastProcessFunction;
import org.apache.flink.util.Collector;
import org.home.streaming.events.KeyedDataPoint;
import org.home.streaming.events.SocketEvent;

import java.util.Map;

public class AmplifierFunction extends KeyedBroadcastProcessFunction<String, KeyedDataPoint, SocketEvent, KeyedDataPoint> {

    private MapStateDescriptor<String, SocketEvent> amplificationDesc;


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        amplificationDesc = new MapStateDescriptor<>(
                "RulesBroadcastState",
                BasicTypeInfo.STRING_TYPE_INFO,
                TypeInformation.of(new TypeHint<SocketEvent>() {
                }));
    }

    @Override
    public void processElement(KeyedDataPoint dataPoint, ReadOnlyContext ctx, Collector<KeyedDataPoint> out) throws Exception {

        boolean rulesWasEmpty = true;
        Iterable<Map.Entry<String, SocketEvent>> rules = ctx.getBroadcastState(amplificationDesc).immutableEntries();
        for (Map.Entry<String, SocketEvent> rule : rules) {

            rulesWasEmpty = false;
            if (rule.getValue().streamKey.equalsIgnoreCase(dataPoint.key)) {
                out.collect(new KeyedDataPoint(dataPoint.value * rule.getValue().amplification, dataPoint.key, dataPoint.timestamp));
            }
            else {
                out.collect(new KeyedDataPoint(dataPoint.value, dataPoint.key, dataPoint.timestamp));
            }
        }

        if (rulesWasEmpty) {
            out.collect(new KeyedDataPoint(dataPoint.value, dataPoint.key, dataPoint.timestamp));
        }
    }

    @Override
    public void processBroadcastElement(SocketEvent event, Context ctx, Collector<KeyedDataPoint> out) throws Exception {
        // store the new pattern by updating the broadcast state
        BroadcastState<String, SocketEvent> bcState = ctx.getBroadcastState(amplificationDesc);
        // storing in MapState with null as VOID default value
        bcState.put(event.streamKey, event);
    }
}
