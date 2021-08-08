package gsr.order.book;

import gsr.order.domain.DataItem;

/**
 *   the data processor of an order book
 **/
public interface DataProcessor<T extends DataItem> {
    void process(T data) throws Exception;
    boolean validate(T data);
}
