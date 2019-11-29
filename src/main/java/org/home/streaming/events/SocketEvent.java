package org.home.streaming.events;

public class SocketEvent {
    public String streamKey;
    public double amplification;

    public SocketEvent() {}

    public SocketEvent(String streamKey, double amplification) {

        this.streamKey = streamKey;
        this.amplification = amplification;
    }

    @Override
    public String toString() {
        return "SocketEvent{" +
                "streamKey='" + streamKey + '\'' +
                ", amplification=" + amplification +
                '}';
    }
}
