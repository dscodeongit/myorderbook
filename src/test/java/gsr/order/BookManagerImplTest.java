package gsr.order;

import gsr.order.domain.*;
import gsr.order.book.OrderBook;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

public class BookManagerImplTest {
    BookManagerImpl orderManager;

    @Before
    public void setUp() {
        BlockingDeque<DataItem> msgQ = new LinkedBlockingDeque<>();
        orderManager = new BookManagerImpl(msgQ);
    }

    @Test
    public void test_process_with_New_order() throws Exception {
        Order order = Order.newOrder("A", Side.BID, "test_order_id", 12345.67, 1.2334);
        orderManager.processData(order);
        Map<String, OrderBook> books = orderManager.getBooks();
        assertEquals(1, books.size());
        assertTrue(books.containsKey(order.getTicker()));
    }

    @Test
    public void test_process_with_Amend_order_Qty() throws Exception {
        Order order = Order.newOrder("A", Side.BID, "test_order_id", 12345.67, 2.2);
        orderManager.processData(order);
        Order orderAmend = Order.amendOrder("A", Side.BID, "test_order_id", 12345.67, 1.1);
        orderManager.processData(orderAmend);
        Map<String, OrderBook> books = orderManager.getBooks();
        assertEquals(1, books.size());
        assertTrue(books.containsKey(order.getTicker()));
    }

    @Test
    public void test_process_with_Cancel_order() throws Exception {
        Order order = Order.newOrder("A", Side.BID, "test_order_id", 12345.67, 2.2);
        orderManager.processData(order);
        Order orderCancel = Order.cancelOrder("A", Side.BID, "test_order_id", 12345.67, 2.2);
        orderManager.processData(orderCancel);
        assertTrue(orderManager.getBooks().isEmpty());
    }

    @Test
    public void test_process_with_Trade() throws Exception {
        Order order = Order.newOrder("A", Side.BID, "test_order_id", 12345.67, 2.2);
        orderManager.processData(order);
        Trade trade = new Trade("test_trade_id", "A", Side.BID, "test_order_id", 12345.67, 1.1);
        orderManager.processData(trade);
        trade = new Trade("test_trade_id", "A", Side.BID, "test_order_id", 12345.67, 1.1);
        orderManager.processData(trade);
        assertTrue(orderManager.getBooks().isEmpty());
    }
}