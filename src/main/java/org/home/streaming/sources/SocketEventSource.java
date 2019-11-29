package org.home.streaming.sources;

import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;

public class SocketEventSource extends SocketTextStreamFunction {

    public SocketEventSource(String hostname, int port, String delimiter, long maxNumRetries) {
        super(hostname, port, delimiter, 1);
    }

    public SocketEventSource(String hostname, int port, String delimiter, long maxNumRetries, long delayBetweenRetries) {
        super(hostname, port, delimiter, maxNumRetries, delayBetweenRetries);
    }

    @Override
    public void run(SourceContext<String> ctx) throws Exception {
        try {
            super.run(ctx);
        } catch (Exception e) {
            System.out.println("Caught Exception");
            e.printStackTrace();
        }
    }
}
