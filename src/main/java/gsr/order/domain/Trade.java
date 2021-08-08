package gsr.order.domain;

public final class Trade extends DataItem {
    private final String tradeId;

    public Trade(String tradeId,String ticker, Side side, String orderId, Double price, Double quantity) {
        super(DataType.TRADE, ticker, side, orderId, price, quantity);
        this.tradeId = tradeId;
    }

    public String getTradeId() {
        return tradeId;
    }

    @Override
    public String toString() {
        return "Trade{" +
                "tradeId=" + tradeId +
                ", ticker='" + ticker + '\'' +
                ", side=" + side +
                ", orderId=" + orderId +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
