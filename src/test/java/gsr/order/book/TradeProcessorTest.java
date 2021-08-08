package gsr.order.book;

import gsr.order.domain.Order;
import gsr.order.domain.Side;
import gsr.order.domain.Trade;
import gsr.order.book.OrderBook;
import gsr.order.book.OrderProcessor;
import gsr.order.book.TradeProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static gsr.utils.Utils.doubleEq;
import static org.junit.Assert.*;

public class TradeProcessorTest {

    TradeProcessor processor;
    OrderProcessor orderProcessor;
    OrderBook book;
    @Before
    public void setUp() {
        book = new OrderBook("testCoin");
        processor = new TradeProcessor(book);
        orderProcessor = new OrderProcessor(book);
    }

    @Test(expected = Exception.class)
    public void process_with_Exception() throws Exception {
        Trade trade = new Trade("test_trade-id", "B", Side.BID, "test_order_id", 12345.67, 1.2334);
        processor.process(trade);
    }

    @Test
    public void validate() {
        Trade trade = new Trade("test_trade-id", null, Side.BID, "test_order_id", 12345.67, 1.2334);
        assertFalse(processor.validate(trade));
    }

    @Test
    public void test_with_Trade() throws Exception{
        Order order = Order.newOrder("testCoin", Side.BID, "test_order_id", 12345.67, 2.2);
        orderProcessor.process(order);
        Trade trade = new Trade("test_trade_id", "testCoin", Side.BID, "test_order_id", 12345.67, 1.1);
        processor.process(trade);
        Map<Double, Map<String, Order>> orders = book.getOrdersView();
        assertTrue(doubleEq(1.1, orders.get(12345.67).get("test_order_id").getQuantity()));
        assertTrue(doubleEq(1.1, book.getBidsView().get(12345.67)));
        trade = new Trade("test_trade_id", "testCoin", Side.BID, "test_order_id", 12345.67, 1.1);
        processor.process(trade);
        assertTrue(book.getBidsView().isEmpty() && book.getAsksView().isEmpty() && book.getOrdersView().isEmpty());
    }

}