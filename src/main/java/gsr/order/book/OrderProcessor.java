package gsr.order.book;

import gsr.order.domain.Order;
import gsr.order.domain.DataType;
import gsr.order.validator.OrderValidator;
import gsr.order.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *   the order processor of an order book
 *   it handles NEW orders, AMEND orders and CANCEL orders
 **/
public class OrderProcessor implements DataProcessor<Order> {
    private static final Logger LOGGER = LogManager.getLogger(OrderProcessor.class);
    private final OrderBook book;
    private final Validator<Order> validator;
    public OrderProcessor(OrderBook book) {
        this.book = book;
        validator = new OrderValidator();
    }

    /**
     * Process the data
     */
    public void process(Order order) throws Exception {
        if (validate(order)){
            if (DataType.ORDER_NEW == order.getType()) {
                processNew(order);
            } else if (DataType.ORDER_AMEND == order.getType()) {
                processAmend(order);
            } else if (DataType.ORDER_CANCEL == order.getType()) {
                processCancel(order);
            }
        } else {
            throw new Exception("data validation failed for " + order + ". book: " + book.getTicker());
        }
    }

    /**
     * Validate the data
     * @param order - the data to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(Order order){
        if (book.getTicker().equals(order.getTicker())) {
            return validator.validate(order);
        } else {
            LOGGER.error(String.format("ticker does not match book ticker[%s]. data details: %s ", book.getTicker(), order));
        }
        return false;
    }

    /**
     * handle New order - assume no 2 orders with OrderType.NEW has same orderId
     * @param order - the order
     */
    protected void processNew(Order order) {
        LOGGER.info("Processing new order, orderId={}", order.getOrderId());
        book.addOrder(order);
    }

    /**
     * handle Order Cancel
     * @param order - the order to cancel
     */
    protected void processCancel(Order order) {
        LOGGER.info("Processing order cancel, orderId={}", order.getOrderId());
        if (book.isOrderExists(order)) {
            book.removeOrder(order);
        } else {
            LOGGER.warn("processCancel: Order does not exists in book. ignored. order = {}", order);
        }
    }

    /**
     * handle Order Amend
     * the new order will replace the old one
     * if there is a Price change then it will be treated as a CANCEL and NEW
     * @param order - the order
     */
    protected void processAmend(Order order) {
        LOGGER.info("Processing order amend, orderId={}", order.getOrderId());
        // need to find the original order by orderId as price might changed in the Amend
        Order origOrder = book.getOrderByOrderId(order.getOrderId());
        //for simplicity, treat AMEND as a CANCEL + a NEW
        if (origOrder!= null) {
                processCancel(origOrder);
                processNew(order);
        }else{
            LOGGER.warn("processAmend: Order does not exists in book. ignored. order = {}", order);
        }
    }
}
