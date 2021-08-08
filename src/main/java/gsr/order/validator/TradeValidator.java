package gsr.order.validator;

import gsr.order.domain.Trade;

public class TradeValidator implements Validator<Trade>{

    /**
     * trade specific validation logic
     * @param trade - the trade
     * @return true if it's valid, false otherwise
     */
    @Override
    public boolean validate(Trade trade) {
        return validateCommon(trade) && trade.getTradeId() != null;
    }
}
