package gsr;

import gsr.marketdata.MarketSubscriber;
import gsr.order.BookManager;
import gsr.order.BookManagerImpl;
import gsr.order.domain.DataItem;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class AppLauncher {

    public static void main(String[] args) {
        BlockingDeque<DataItem> msgQ = new LinkedBlockingDeque<>();
        BookManager<DataItem> orderManager = new BookManagerImpl(msgQ);
        String ticker = args[0];
        MarketSubscriber subscriber = new MarketSubscriber(ticker, msgQ);
        subscriber.start();
    }
}
