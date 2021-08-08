package gsr.order;

import gsr.order.book.OrderBook;

import java.util.Map;

public interface BookManager <T> {
    void processData(T data) throws Exception;
    void shutDown();
    Map<String, OrderBook> getBooks();
}
