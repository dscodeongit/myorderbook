package gsr.marketdata;

import gsr.order.domain.Order;
import gsr.order.domain.DataType;
import gsr.order.domain.DataItem;
import gsr.order.domain.Side;
import io.contek.invoker.coinbasepro.api.websocket.market.MatchesChannel;
import io.contek.invoker.commons.api.websocket.ConsumerState;
import io.contek.invoker.commons.api.websocket.ISubscribingConsumer;
import io.contek.invoker.commons.api.websocket.SubscriptionState;
import io.contek.invoker.coinbasepro.api.websocket.market.MatchesChannel.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingDeque;

/**
 * MarketMatch data consumer
 * use marketMatch feed for testing
 */
public class MarketMatchConsumer implements ISubscribingConsumer<Message> {
    private final BlockingDeque<DataItem> msgQ;
    private static final Logger LOGGER = LogManager.getLogger(MarketMatchConsumer.class);

    public MarketMatchConsumer(BlockingDeque<DataItem> msgQ) {
        this.msgQ = msgQ;
    }

    @Override
    public void onNext(MatchesChannel.Message message) {
        Order order = parseOrder(message);
        msgQ.offer(order);
    }

    // for testing purpose, treat each match as an NEW order
    private Order parseOrder(Message message) {
        return Order.newOrder(message.product_id, Side.of(message.side), message.taker_order_id, message.price, message.size);
    }

    @Override
    public ConsumerState getState() {
        return ConsumerState.ACTIVE;
    }

    @Override
    public void onStateChange(SubscriptionState state) {
        if (state == SubscriptionState.SUBSCRIBED) {
            LOGGER.info("Start receiving trade data");
        }
    }
}
