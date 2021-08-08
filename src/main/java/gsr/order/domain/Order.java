package gsr.order.domain;

import java.util.Objects;

public final class Order extends DataItem {
    public Order(DataType type,String ticker, Side side, String orderId, Double price, Double quantity) {
        super(type, ticker, side, orderId, price, quantity);
    }

    public static Order newOrder(String ticker, Side side, String orderId, Double price, Double quantity){
        return new Order(DataType.ORDER_NEW, ticker, side, orderId, price, quantity);
    }

    public static Order amendOrder(String ticker, Side side, String orderId, Double price, Double quantity){
        return new Order(DataType.ORDER_AMEND, ticker, side, orderId, price, quantity);
    }


    public static Order cancelOrder(String ticker, Side side, String orderId, Double price, Double quantity){
        return new Order(DataType.ORDER_CANCEL, ticker, side, orderId, price, quantity);
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return Objects.equals(ticker, order.ticker) && type == order.type && side == order.side && Objects.equals(orderId, order.orderId) && Objects.equals(price, order.price) && Objects.equals(quantity, order.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticker, type, side, orderId, price, quantity);
    }

    @Override
    public String toString() {
        return "Order{" +
                "ticker='" + ticker + '\'' +
                ", type=" + type +
                ", side=" + side +
                ", orderId=" + orderId +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
