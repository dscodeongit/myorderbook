package gsr.order.validator;

import gsr.order.domain.DataItem;

public interface Validator<T extends DataItem> {
    default boolean validateCommon(T item){
        return item.getOrderId() != null &&
                item.getQuantity() != null &&
                item.getTicker() != null &&
                item.getPrice() != null &&
                item.getSide() != null;
    }
    boolean validate(T item);
}
