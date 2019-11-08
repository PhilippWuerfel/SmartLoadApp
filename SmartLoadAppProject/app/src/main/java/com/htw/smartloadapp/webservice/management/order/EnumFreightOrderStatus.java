package com.htw.smartloadapp.webservice.management.order;

public enum EnumFreightOrderStatus{
    // enums needs to be discussed
    BEFORE_PACKING,
    ON_PACKING,
    READY_FOR_DELIVERY,
    ON_DELIVERY,
    ORDER_FINISHED
    // optional additional status: ORDER_REBOOKED (sth. like that, if Customers wants to rebook same order)
}
