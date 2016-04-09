package com.tbb.analytics.flume.twitter.source;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.annotations.InterfaceAudience;
import org.apache.flume.annotations.InterfaceStability;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import twitter4j.JSONException;
import twitter4j.JSONObject;
import twitter4j.TwitterObjectFactory;

import java.nio.charset.Charset;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Riyaz A Shaikh on 4/2/2016.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public class FilterStreamSource extends AbstractSource implements Configurable, EventDrivenSource {

    private BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
    private StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
    private Client client;

    @Override
    public void configure(Context context) {
        String consumerKey = Preconditions.checkNotNull(context.getString("consumerKey"), "Consumer Key cannot be empty. Please check flume.conf file.");
        String consumerSecret = Preconditions.checkNotNull(context.getString("consumerSecret"), "Consumer Secret cannot be empty. Please check flume.conf file.");
        String accessToken = Preconditions.checkNotNull(context.getString("accessToken"),"Access Token cannot be empty. Please check flume.conf file.");
        String accessTokenSecret = Preconditions.checkNotNull(context.getString("accessTokenSecret"),"Access Token Secret cannot be empty. Please check flume.conf file.");
        Authentication auth = new OAuth1(consumerKey, consumerSecret, accessToken, accessTokenSecret);

        String keywords = Preconditions.checkNotNull(context.getString("trackterms"), "Track term cannot be empty. Please check flume.conf file.");
        endpoint.trackTerms(Lists.newArrayList(keywords));
        client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();
    }

    @Override
    public synchronized void start() {
        final ChannelProcessor channel = getChannelProcessor();
        client.connect();
        while (!client.isDone()) {
            try {
                String msg = queue.take();
                if (msg != null) {
                    Event event = EventBuilder.withBody(msg, Charset.forName("UTF-8"));
                    channel.processEvent(event);
                }
            } catch (InterruptedException e) {
                System.err.println("Exception is thrown while reading from queue.");
            }
        }
    }

    @Override
    public synchronized void stop() {
        client.stop();
    super.stop();
    }
}
