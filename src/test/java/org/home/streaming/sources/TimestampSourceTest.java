package org.home.streaming.sources;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext;
import org.home.streaming.events.DataPoint;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class TimestampSourceTest {

    private TimestampSource timestampSource;

    @Test
    public void foo() throws Exception {

        SourceContext sourceContext = mock(SourceContext.class);
        when(sourceContext.getCheckpointLock()).thenReturn(new Object());
        final List<DataPoint> collectedEvents = new ArrayList<>(10000);


        doAnswer(invocation -> {
            collectedEvents.add((DataPoint) invocation.getArguments()[0]);
            return null;
        }).when(sourceContext).collectWithTimestamp(any(DataPoint.class), anyLong());



        timestampSource = new TimestampSource(10, 1);
        timestampSource.open(mock(Configuration.class));
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                timestampSource.run(sourceContext);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(1500);
        timestampSource.cancel();
        System.out.println(collectedEvents);

    }

}