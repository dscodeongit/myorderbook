package gsr.order.domain;

public enum Side {
    BID,
    ASK,
    UNKNOWN;

    public static Side of(String side){
        if ("BUY".equalsIgnoreCase(side) || "BID".equalsIgnoreCase(side)){
            return BID;
        }else if("SELL".equalsIgnoreCase(side) || "ASK".equalsIgnoreCase(side)){
            return ASK;
        }else{
            return UNKNOWN;
        }
    }
}
