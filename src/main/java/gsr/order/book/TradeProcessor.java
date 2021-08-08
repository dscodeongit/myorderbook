package gsr.order.book;

import gsr.order.domain.Order;
import gsr.order.domain.Trade;
import gsr.order.validator.TradeValidator;
import gsr.order.validator.Validator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *  the trade processor of an order book for filled orders
 **/
public class TradeProcessor implements DataProcessor<Trade> {
    private static final Logger LOGGER = LogManager.getLogger(TradeProcessor.class);
    private final OrderBook book;
    private final Validator<Trade> validator;
    public TradeProcessor(OrderBook book) {
        this.book = book;
        validator = new TradeValidator();
    }

    /**
     * Process the data
     */
    public void process(Trade trade) throws Exception {
        if (validate(trade)){
            LOGGER.info("Processing new trade, tradeId={}, orderId={}", trade.getTradeId(), trade.getOrderId());
            Order order = book.ordersAtLevel(trade.getPrice()).get(trade.getOrderId());
            double unFilled = order.getQuantity() - trade.getQuantity();
            if (unFilled <= 0) {
                book.removeOrder(order);
            } else {
                order.setQuantity(unFilled);
                book.updateBook(trade.getSide(), trade.getPrice(), -trade.getQuantity());
            }
        } else {
            throw new Exception("data validation failed for " + trade + ". book: " + book.getTicker());
        }
    }

    /**
     * Validate the data
     * @param trade - the data to validate
     * @return true if valid, false otherwise
     */
    public boolean validate(Trade trade){
        if (book.getTicker().equals(trade.getTicker())) {
            return validator.validate(trade);
        } else {
            LOGGER.error(String.format("ticker does not match book ticker[%s]. data details: %s ", book.getTicker(), trade));
        }
        return false;
    }
}
