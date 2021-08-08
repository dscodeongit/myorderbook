package gsr.order;

import gsr.order.domain.DataItem;
import gsr.order.book.OrderBook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

/**
 * The order Manager that manages orders
 * it uses a single worker thread to handle all incoming orders and trades
 * it also manages incoming messages from other sources
 * I assume the incoming messages can be one of the following types:
 * 1. New order message (Order with OrderType.NEW)
 * 2. Order Cancel message (Order with OrderType.CANCEL)
 * 3. Order Amend message (Order with OrderType.AMEND)
 * 4. Trade message
 * A gateway parser (not provided) on the producer side of the queue should parse incoming messages into one of the
 * above message Types and put to the queue
 */
public class BookManagerImpl implements BookManager<DataItem> {
    private static final Logger LOGGER = LogManager.getLogger(BookManagerImpl.class);
    private final Map<String, OrderBook> books;

    // the incoming Queue for incoming orders or trades, this should be injected on start up
    // in real app this might be replaced with a messaging queue, like JMS queue
    private final BlockingDeque<DataItem> msgQ;

    private volatile boolean  shutDown = false;

    public BookManagerImpl(BlockingDeque<DataItem> orderQ) {
        this.msgQ = orderQ;
        this.books = new HashMap<>();

        new Thread(this::start).start();
    }

    public void start() {
        while(!shutDown){
            DataItem item = null;
            try {
                item = msgQ.take();
                LOGGER.info("Received new data - {}", item);
                processData(item);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                LOGGER.error("process data failed, errMsg = {}, data details : {} ", e.getMessage(), item);
            }
        }
    }

    @Override
    public void processData(DataItem data) throws Exception {
        String bookId = data.getTicker();
        books.putIfAbsent(bookId, new OrderBook(bookId));
        books.get(bookId).onData(data);
        //print the book for each data event
        books.get(bookId).printBook();
        if (this.books.get(bookId).getBidsView().isEmpty() && this.books.get(bookId).getAsksView().isEmpty()){
            this.books.remove(bookId);
        }
    }

    public void shutDown(){
        this.shutDown = true;
    }

    public Map<String, OrderBook> getBooks() {
        return Collections.unmodifiableMap(books);
    }
}
