package com.ctrip.framework.traffic.netty.client;

import com.ctrip.framework.traffic.netty.protocol.request.MessageRequestPacket;
import com.ctrip.framework.traffic.netty.protocol.response.MessageResponsePacket;
import com.ctrip.framework.traffic.utils.ThreadUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by jixinwang on 2023/9/8
 */
public class MessageImpl implements Message {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Logger delay = LoggerFactory.getLogger("delayLogger");

    private static final String _4B = "abcd";
    //one mysql rows_event max size is 8KB(2^10 * 2^6)
    private static final String _8KB = _4B.repeat((int) (Math.pow(2, 10) * 2));
    private static final int INIT = 2 * 1000;

    private List<MessageRequestPacket> requests = Lists.newArrayList();
    private Map<Integer, Entity> receivedMap = Maps.newConcurrentMap();
    private ScheduledExecutorService sendScheduledExecutor;
    private ExecutorService receivedExecutor;

    private int clientId;
    private int period;
    private double roundCount;

    public MessageImpl(int bandWidth, int period, int clientId) {
        this.clientId = clientId;
        if (period / 100 > 0 && period / 100 <= 10) {
            this.period = period / 100 * 100;
        } else {
            this.period = 200;
        }
        int round = 1000 / this.period;
        this.roundCount = Math.pow(2, 20) / Math.pow(2, 16) / round;
        if (bandWidth > 0 && bandWidth <= 1000) {
            roundCount = roundCount * bandWidth;
            logger.info("[client][{}] bandWidth size: {}Mbps, roundCount: {}, period: {}", clientId, bandWidth, roundCount, this.period);
        } else {
            logger.info("[client][{}] invalid bandWidth size: {}Mbps, use 1Mbps, roundCount: {}, period: {}", clientId, bandWidth, roundCount, this.period);
        }
    }

    @Override
    public void start() {
        sendScheduledExecutor = ThreadUtils.newSingleThreadScheduledExecutor("sender");
        receivedExecutor = ThreadUtils.newSingleThreadScheduledExecutor("receiver");
        logger.info("[client][{}] start, send bits size: {}, roundCount: {}, period: {}", clientId, _8KB.getBytes().length * 8, roundCount, period);
        for (int i = 0; i < roundCount; i++) {
            MessageRequestPacket request = new MessageRequestPacket(i, _8KB);
            Entity entity = new Entity(true, 0);
            receivedMap.put(i, entity);
            requests.add(request);
        }
    }

    @Override
    public void send(Channel channel) {
        sendScheduledExecutor.scheduleWithFixedDelay(() -> {
            long start = System.currentTimeMillis();
            int sendCount = 0;
            for (MessageRequestPacket request : requests) {
                try {
                    int seq = request.getSeq();
                    Entity entity = receivedMap.get(seq);
                    if (!entity.isReceived()) {
                        logger.debug("[client][{}] seq: {} not received", clientId, seq);
                        continue;
                    }
                    sendCount++;
                    logger.debug("[client][{}] seq: {} has received", clientId, seq);
                    entity.setReceived(false);
                    entity.setSendTime(System.currentTimeMillis());
                    channel.writeAndFlush(request);
                } catch (Exception e) {
                    logger.warn("[client][{}] received error", clientId, e);
                }
            }
            logger.info("[client][{}] send cost: {} ms, count: {}", clientId, System.currentTimeMillis() - start, sendCount);
        }, INIT, period, TimeUnit.MILLISECONDS);
    }

    @Override
    public void receive(MessageResponsePacket response) {
        receivedExecutor.execute(() -> {
            int seq = response.getSeq();
            Entity entity = receivedMap.get(seq);
            entity.setReceived(true);
            long delayTime = System.currentTimeMillis() - entity.getSendTime();
            delay.info("[{}]delay: {}ms", clientId, delayTime);
        });
    }

    @Override
    public void stop() {
        if (sendScheduledExecutor != null) {
            sendScheduledExecutor.shutdownNow();
            sendScheduledExecutor = null;
        }

        if (receivedExecutor != null) {
            receivedExecutor.shutdownNow();
            receivedExecutor = null;
        }
    }

    private static class Entity {
        private boolean received;
        private long sendTime;

        public Entity(boolean received, long sendTime) {
            this.received = received;
            this.sendTime = sendTime;
        }

        public boolean isReceived() {
            return received;
        }

        public void setReceived(boolean received) {
            this.received = received;
        }

        public long getSendTime() {
            return sendTime;
        }

        public void setSendTime(long sendTime) {
            this.sendTime = sendTime;
        }
    }
}
