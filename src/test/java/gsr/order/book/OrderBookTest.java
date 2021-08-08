package gsr.order.book;

import gsr.order.domain.Order;
import gsr.order.domain.Side;
import gsr.order.book.OrderBook;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static gsr.utils.Utils.doubleEq;
import static org.junit.Assert.*;

public class OrderBookTest {

    OrderBook testBook;

    @Before
    public void setUp() {
        testBook = new OrderBook("testCoin");
    }

    @Test
    public void updateBook_OneOrder() {
        test_with_One_order(1.01, 0.01, Side.BID);
    }

    @Test
    public void updateBook_OneAsk() {
        test_with_One_order(1.01, 0.01, Side.ASK);
    }

    private void test_with_One_order(Double price, Double qty, Side side){
        Map<Double, Double> sideOrders = Side.ASK == side? testBook.getAsksView(): testBook.getBidsView();
        Map<Double, Double> counterSideOrders = Side.ASK == side? testBook.getBidsView(): testBook.getAsksView();
        testBook.updateBook(side, price, qty);
        assertTrue(sideOrders.containsKey(price));
        assertEquals(sideOrders.get(price), qty);
        assertEquals(sideOrders.size(), 1);
        assertTrue(counterSideOrders.isEmpty());
    }

    @Test
    public void updateBook_moreOrders() {
        Map<Double, Double> sideOrders = testBook.getBidsView();
        testBook.updateBook(Side.BID, 9999.01, 0.01);
        testBook.updateBook(Side.BID, 9999.01, 0.02);
        testBook.updateBook(Side.BID, 9999.01, 0.03);
        assertTrue(doubleEq(sideOrders.get(9999.01),0.06));
        testBook.updateBook(Side.BID, 9999.01, -0.04);
        assertTrue(doubleEq(sideOrders.get(9999.01), (0.02)));
        testBook.updateBook(Side.BID, 9999.01, -0.02);
        assertFalse(sideOrders.containsKey(9999.01));
    }

    @Test
    public void updateBook_bid_overflow() {
        Map<Double, Double> sideOrders = testBook.getBidsView();
        testBook.updateBook(Side.BID, 9999.0, 0.01);
        testBook.updateBook(Side.BID, 9999.01, 0.01);
        testBook.updateBook(Side.BID, 9999.02, 0.02);
        testBook.updateBook(Side.BID, 9999.03, 0.03);
        testBook.updateBook(Side.BID, 9999.04, 0.01);
        testBook.updateBook(Side.BID, 9999.05, 0.02);
        testBook.updateBook(Side.BID, 9999.06, 0.03);
        testBook.updateBook(Side.BID, 9999.07, 0.02);
        testBook.updateBook(Side.BID, 9999.08, 0.03);
        testBook.updateBook(Side.BID, 9999.09, 0.01);
        assertEquals(sideOrders.size(), 10);
        assertTrue(sideOrders.containsKey(9999.0));
        testBook.updateBook(Side.BID, 9999.10, 0.02);
        assertFalse(sideOrders.containsKey(9999.0));
        assertTrue(sideOrders.containsKey(9999.10));
        assertEquals(sideOrders.size(), 10);
    }

    @Test
    public void updateBook_ask_overflow() {
        Map<Double, Double> sideOrders = testBook.getAsksView();
        testBook.updateBook(Side.ASK, 9999.10, 0.01);
        testBook.updateBook(Side.ASK, 9999.01, 0.01);
        testBook.updateBook(Side.ASK, 9999.02, 0.02);
        testBook.updateBook(Side.ASK, 9999.03, 0.03);
        testBook.updateBook(Side.ASK, 9999.04, 0.01);
        testBook.updateBook(Side.ASK, 9999.05, 0.02);
        testBook.updateBook(Side.ASK, 9999.06, 0.03);
        testBook.updateBook(Side.ASK, 9999.07, 0.02);
        testBook.updateBook(Side.ASK, 9999.08, 0.03);
        testBook.updateBook(Side.ASK, 9999.09, 0.01);
        assertEquals(sideOrders.size(), 10);
        assertTrue(sideOrders.containsKey(9999.10));
        testBook.updateBook(Side.ASK, 9999.0, 0.02);
        assertFalse(sideOrders.containsKey(9999.10));
        assertTrue(sideOrders.containsKey(9999.0));
        assertEquals(sideOrders.size(), 10);
    }

    @Test(expected = Exception.class)
    public void test_with_Exception() throws Exception {
        Order order = Order.newOrder("A", Side.BID, "test_order_id", 12345.67, 1.2334);
        testBook.onData(order);
    }
}