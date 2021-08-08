package gsr.marketdata;

import gsr.order.domain.DataItem;
import io.contek.invoker.coinbasepro.api.ApiFactory;
import io.contek.invoker.coinbasepro.api.websocket.market.MarketWebSocketApi;

import java.util.concurrent.BlockingDeque;

/**
 * MarketData Subscriber
 */
public class MarketSubscriber {
    private final String ticker; //"ETH-USDT"
    private final BlockingDeque<DataItem> msgQ;

    public MarketSubscriber(String ticker, BlockingDeque<DataItem> msgQ) {
        this.ticker = ticker;
        this.msgQ = msgQ;
    }

    public void start(){
        MarketWebSocketApi api = ApiFactory.getMainNetDefault().ws().market();
        api.getMatchesChannel(this.ticker).addConsumer(new MarketMatchConsumer(msgQ));
    }
}
