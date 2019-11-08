package com.htw.smartloadapp.webservice.task.bodies;

import com.htw.smartloadapp.webservice.management.order.EnumFreightOrderStatus;

public class FreightOrderStatusTask {
    private String freightOrderId;
    private EnumFreightOrderStatus freightOrderStatus;
    private String userId;
    private String timestamp;

    public FreightOrderStatusTask(String freightOrderId, EnumFreightOrderStatus freightOrderStatus, String userId, String timestamp) {
        this.freightOrderId = freightOrderId;
        this.freightOrderStatus = freightOrderStatus;
        this.userId = userId;
        this.timestamp = timestamp;
    }
}
