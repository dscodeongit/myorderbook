package gsr.order.book;

import gsr.order.domain.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DecimalFormat;
import java.util.*;

import static gsr.utils.Utils.doubleEq;

/**
 *   the order book of an instrument
 *   As per requirement, it only keeps MAX_LEVEL (10) levels of aks and bids (my understanding is to
 *   keep 10 levels on each side), levels beyond MAX_LEVEL will be dropped
 *   it uses a TreeMap to store bids in descending order, and asks in ascending order
 *   it also maintains the order list for this instruments at each price level
 *   it delegate to dataProcessor to handle incoming data
 */
public final class OrderBook {
    private static final Logger LOGGER = LogManager.getLogger(OrderBook.class);
    private final String ticker;
    // the accumulative bids
    private final NavigableMap<Double, Double> bids;
    // the accumulative asks
    private final NavigableMap<Double, Double> asks;
    //the orders of this book. orders at each price level are kept in a
    //LinkedHashMap, so maintains insertion order, also provide fast access
    private final Map<Double, Map<String, Order>> orders;

    public final int MAX_LEVEL = 10;
    private final OrderProcessor orderProcessor;
    private final TradeProcessor tradeProcessor;

    DecimalFormat df = new DecimalFormat("###.########");

    public OrderBook(String ticker) {
        this.ticker = ticker;
        this.asks = new TreeMap<>();
        this.bids = new TreeMap<>(Collections.reverseOrder());
        this.orders = new HashMap<>();
        this.orderProcessor = new OrderProcessor(this);
        this.tradeProcessor = new TradeProcessor(this);
    }

    public void onData(DataItem data) throws Exception {
        if (DataType.TRADE == data.getType())
            tradeProcessor.process((Trade) data);
        else
            orderProcessor.process((Order) data);
    }

    /**
     * Add a order to the book
     * @param order - the order to add
     */
    protected void addOrder(Order order){
        orders.putIfAbsent(order.getPrice(), new LinkedHashMap<>());
        ordersAtLevel(order.getPrice()).put(order.getOrderId(), order);
        updateBook(order.getSide(), order.getPrice(), order.getQuantity());
    }

    /**
     * remove order from the book
     * @param order - the order to removed
     * @return - return the removed order
     */
    protected Order removeOrder(Order order){
        Map<String, Order> ordersAtLevel = ordersAtLevel(order.getPrice());
        Order removed = ordersAtLevel.remove(order.getOrderId());
        if(ordersAtLevel.isEmpty()){
            this.orders.remove(order.getPrice());
        }
        updateBook(removed.getSide(), removed.getPrice(), -removed.getQuantity());
        return removed;
    }

    /**
     * update the book with new orders
     * @param side - the side of the order
     * @param price - the price of the order
     * @param quantity - the quantity of the order
     */
    protected void updateBook(Side side, double price, double quantity){
        if(Side.ASK == side)
            updateLevel(price, quantity, this.asks);
        else if (Side.BID == side)
            updateLevel(price, quantity, this.bids);
    }

    /**
     * update either the bid or ask sides
     * @param price - the price level
     * @param quantity - the new quantity
     * @param sideLevels -  contains the orders of the side to update
     */
    protected void updateLevel(double price, double quantity, NavigableMap<Double, Double> sideLevels) {
        boolean levelAlreadyExists = sideLevels.containsKey(price);
        double newQty = sideLevels.getOrDefault(price, 0d) + quantity;
        if (doubleEq(newQty, 0d)){
            sideLevels.remove(price);
        }else {
            sideLevels.put(price, newQty);
        }
        // evict level beyond MAX_LEVEL
        if(sideLevels.size() > MAX_LEVEL) {
            sideLevels.pollLastEntry();
        }
    }

    /**
     * Print the order book to console
     * I assume the test request is to print the accumulative bids and asks instead of the actual orders
     * it will print bids side in descending order and asks side in ascending order
     */
    public void printBook() {
        System.out.println("Printing book : " + this.ticker);
        System.out.println("*********** ASKS STARTS ***********");
        Map<Double, Double> asks = getAsksView();
        for (Double level : asks.keySet()){
            System.out.println(level +" : "+ df.format(asks.get(level)));
        }
        System.out.println("*********** ASKS ENDS ************");
        System.out.println("*********** BIDS STARTS ***********");
        Map<Double, Double> bids = getBidsView();
        for (Double level : bids.keySet()){
            System.out.println(level +" : "+ df.format(bids.get(level)));
        }
        System.out.println("*********** BIDS ENDS ************");

    }

    /**
     * @param price - the price level
     * @return all orders at the the specific price level
     */
    protected Map<String, Order> ordersAtLevel(Double price){
        if(orders.containsKey(price)){
            return orders.get(price);
        }
        return null;
    }

    /**
     * @param price - the price
     * @param orderId - the orderId
     * @return the order in the book with the specific price and orderId
     */
    public Order getOrder(Double price, String orderId){
        return orders.containsKey(price) ? ordersAtLevel(price).get(orderId) : null;
    }

    public boolean isOrderExists(Order order){
        return getOrder(order.getPrice(), order.getOrderId()) != null;
    }

    /**
     * @param orderId - the orderId
     * @return the order in the book with the specified orderId
     */
    public Order getOrderByOrderId(String orderId){
        for (Double price : this.orders.keySet()){
            if(ordersAtLevel(price).containsKey(orderId)){
                return ordersAtLevel(price).get(orderId);
            }
        }
        return null;
    }

    /**
     * @return the instrument of this book
     */
    public String getTicker() {
        return ticker;
    }

    public NavigableMap<Double, Double> getBookView(Side side){
        return  Side.BID == side ? getBidsView() : getAsksView();
    }

    /**
     * @return an immutable view all bids
     */
    public NavigableMap<Double, Double> getBidsView() {
        return Collections.unmodifiableNavigableMap(bids);
    }

    /**
     * @return an immutable view all asks
     */
    public NavigableMap<Double, Double> getAsksView() {
        return Collections.unmodifiableNavigableMap(asks);
    }

    /**
     * @return an immutable views af all orders at different prices
     */
    public Map<Double, Map<String, Order>> getOrdersView() {
        return Collections.unmodifiableMap(orders);
    }

    /**
     * @return an immutable views af all orders at specific prices level ordered by insertion time
     */
    public Map<String, Order> getOrdersAtPrice(Double price) {
        return ordersAtLevel(price) == null ? Collections.emptyMap() : Collections.unmodifiableMap(ordersAtLevel(price));
    }
}
