package com.tbb.analytics.flume.twitter.source;

import org.apache.flume.Context;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.conf.Configurable;
import org.apache.flume.source.AbstractSource;

/**
 * Created by Riyaz A Shaikh on 4/2/2016.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public class SampleStreamSource extends AbstractSource implements Configurable, EventDrivenSource {


    @Override
    public void configure(Context context) {

    }

    @Override
    public synchronized void start() {

    }

    @Override
    public synchronized void stop() {

    }
}
