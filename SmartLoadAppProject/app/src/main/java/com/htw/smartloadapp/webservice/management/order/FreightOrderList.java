package com.htw.smartloadapp.webservice.management.order;

import com.htw.smartloadapp.webservice.restservice.model.FreightOrderModel;

import java.util.ArrayList;

public class FreightOrderList {
    private static final String TAG = "FreightOrderList";
    // Singleton
    private static FreightOrderList freightOrderList_instance = null;

    private ArrayList<FreightOrderModel> freightOrders;
    private ArrayList<FreightOrderModel> freightOrdersForDriver;
    private ArrayList<FreightOrderModel> freightOrdersForPacker;

    private FreightOrderList() {

    }

    public static FreightOrderList getInstance(){
        if (freightOrderList_instance == null){
            freightOrderList_instance = new FreightOrderList();
        }
        return freightOrderList_instance;
    }

    public void setFreightOrders(ArrayList<FreightOrderModel> freightOrders) {
        this.freightOrdersForDriver = new ArrayList<>();
        this.freightOrdersForPacker = new ArrayList<>();
        this.freightOrders = freightOrders;
        FreightOrderModel freightOrder;
        for(int i = 0; i < freightOrders.size(); i++){
            freightOrder = freightOrders.get(i);
            if (freightOrder.getFreightOrderStatus() == EnumFreightOrderStatus.READY_FOR_DELIVERY){
                this.freightOrdersForDriver.add(freightOrder);
            }else if(freightOrder.getFreightOrderStatus() == EnumFreightOrderStatus.BEFORE_PACKING){
                this.freightOrdersForPacker.add(freightOrder);
            }
        }
    }

    public ArrayList<FreightOrderModel> getFreightOrders() {
        return freightOrders;
    }
    public ArrayList<FreightOrderModel> getFreightOrdersDriver(){
        //return freightOrders;
        return freightOrdersForDriver;
    }
    public ArrayList<FreightOrderModel> getFreightOrdersPacker(){
        //return freightOrders;
        return freightOrdersForPacker;
    }




}
