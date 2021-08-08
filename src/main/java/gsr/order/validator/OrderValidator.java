package gsr.order.validator;

import gsr.order.domain.Order;

public class OrderValidator implements Validator<Order>{

    /**
     * order specific validation logic
     * @param order - the order
     * @return true if it's valid, false otherwise
     */
    @Override
    public boolean validate(Order order) {
        return validateCommon(order) &&
                order.getType() != null;
    }
}
