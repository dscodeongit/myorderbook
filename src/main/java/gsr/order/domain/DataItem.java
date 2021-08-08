package gsr.order.domain;

public abstract class DataItem {
    protected final DataType type;
    protected final String ticker;
    protected final Side side;
    protected final String orderId;
    protected final Double price;
    protected Double quantity;

    public DataItem(DataType type, String ticker, Side side, String orderId, Double price, Double quantity) {
        this.type = type;
        this.ticker = ticker;
        this.side = side;
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
    }

    public DataType getType() { return type; }

    public String getTicker() {
        return ticker;
    }

    public Side getSide() {
        return side;
    }

    public String getOrderId() {
        return orderId;
    }

    public Double getPrice() {
        return price;
    }

    public Double getQuantity() {
        return quantity;
    }


}