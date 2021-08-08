package gsr.order.book;

import gsr.order.domain.Order;
import gsr.order.domain.Side;
import gsr.order.book.OrderBook;
import gsr.order.book.OrderProcessor;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static gsr.utils.Utils.doubleEq;
import static org.junit.Assert.*;

public class OrderProcessorTest {

    OrderProcessor processor;
    OrderBook book;

    @Before
    public void setUp() {
        book = new OrderBook("testCoin");
        processor = new OrderProcessor(book);
    }

    @Test(expected = Exception.class)
    public void process_with_Exception() throws Exception {
        Order order = Order.newOrder("B", Side.BID, "test_order_id", 12345.67, 1.2334);
        processor.process(order);
    }

    @Test
    public void validate() {
        Order order = Order.newOrder("B", Side.BID, "test_order_id", 12345.67, 1.2334);
        assertFalse(processor.validate(order));
        order = Order.newOrder("testCoin", Side.BID, null, 12345.67, 1.2334);
        assertFalse(processor.validate(order));
    }

    @Test
    public void processNew() {
        Order order = Order.newOrder("testCoin", Side.BID, "test_order_id", 12345.67, 1.2334);
        processor.processNew(order);
        Map<Double, Map<String, Order>> orders = book.getOrdersView();
        assertEquals(1, orders.size());
        assertTrue(orders.containsKey(12345.67));
        assertEquals(order, orders.get(12345.67).get("test_order_id"));
        assertTrue(book.getBidsView().containsKey(12345.67));
        assertTrue(doubleEq(1.2334, book.getBidsView().get(12345.67)));
    }

    @Test
    public void test_with_Amend_order_Qty() throws Exception {
        Order order = Order.newOrder("testCoin", Side.BID, "test_order_id", 12345.67, 2.2);
        processor.process(order);
        Order orderAmend = Order.amendOrder("testCoin", Side.BID, "test_order_id", 12345.67, 1.1);
        processor.processAmend(orderAmend);
        Map<Double, Map<String, Order>> orders = book.getOrdersView();
        assertEquals(1, orders.size());
        assertTrue(orders.containsKey(12345.67));
        assertTrue(doubleEq(1.1, orders.get(12345.67).get("test_order_id").getQuantity()));
        assertTrue(book.getBidsView().containsKey(12345.67));
        assertTrue(doubleEq(1.1, book.getBidsView().get(12345.67)));
    }

    @Test
    public void test_with_Amend_order_Price() throws Exception {
        Order order = Order.newOrder("testCoin", Side.BID, "test_order_id", 12345.67, 2.2);
        processor.process(order);
        Order orderAmend = Order.amendOrder("testCoin", Side.BID, "test_order_id", 12345.66, 2.2);
        processor.processAmend(orderAmend);
        Map<Double, Map<String, Order>> orders = book.getOrdersView();
        assertTrue(orders.containsKey(12345.66));
        assertFalse(orders.containsKey(12345.67));
        assertTrue(book.getBidsView().containsKey(12345.66));
        assertFalse(book.getBidsView().containsKey(12345.67));
        assertTrue(doubleEq(2.2, orders.get(12345.66).get("test_order_id").getQuantity()));
        assertTrue(doubleEq(2.2, book.getBidsView().get(12345.66)));
    }

    @Test
    public void test_with_Cancel_order() throws Exception {
        Order order = Order.newOrder("testCoin", Side.BID, "test_order_id", 12345.67, 2.2);
        processor.process(order);
        Order orderCancel = Order.cancelOrder("testCoin", Side.BID, "test_order_id", 12345.67, 2.2);
        processor.processCancel(orderCancel);
        assertTrue(book.getBidsView().isEmpty() && book.getAsksView().isEmpty() && book.getOrdersView().isEmpty());
    }

    @Test
    public void test_bids_accumulation() throws Exception{
        test_order_accumulation(Side.BID);
    }

    @Test
    public void test_asks_accumulation() throws Exception{
        test_order_accumulation(Side.ASK);
    }

    private void test_order_accumulation(Side side) throws Exception {
        Side counterSide = Side.ASK == side ? Side.BID : Side.ASK;
        Order order = Order.newOrder("testCoin", side, "test_order_id_0", 12345.67, 0.07077972);
        processor.process(order);
        order = Order.newOrder("testCoin", side, "test_order_id_1", 12345.67, 0.10014625);
        processor.process(order);
        order = Order.newOrder("testCoin", side, "test_order_id_2", 12345.67, 0.03391169);
        processor.process(order);
        assertTrue(doubleEq((0.07077972+0.10014625+0.03391169),  book.getBookView(side).get(12345.67)));
        order = Order.cancelOrder("testCoin", side, "test_order_id_2", 12345.67, 0.03391169);
        processor.process(order);
        assertTrue(doubleEq((0.07077972+0.10014625),  book.getBookView(side).get(12345.67)));
        order = Order.amendOrder("testCoin", side, "test_order_id_1", 12345.67, 0.03391169);
        processor.process(order);
        assertTrue(doubleEq((0.07077972+0.03391169),  book.getBookView(side).get(12345.67)));
        order = Order.cancelOrder("testCoin", side, "test_order_id_1", 12345.67, 0.03391169);
        processor.process(order);
        assertTrue(doubleEq((0.07077972),  book.getBookView(side).get(12345.67)));
        order = Order.newOrder("testCoin", side, "test_order_id_3", 12345.67, 0.10014626);
        processor.process(order);
        order = Order.newOrder("testCoin", counterSide, "test_order_id_4", 12345.67, 0.10014628);
        processor.process(order);
        assertTrue(doubleEq((0.07077972+0.10014626),  book.getBookView(side).get(12345.67)));
    }
}