package com.yoavgibri.myincome.Models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yoav on 16/07/16.
 */
public class Session {

    public String clientName;
    public String clientCalendarName;
//    public int jobType;
    public String type;
    public int amount;
    public int dayOfMonth;
    public String comment;
    public boolean isPaid;
//    public long insertTime;

    public Session() {
    }

    public Session(String clientCalendarName, int dayOfMonth) {
        this.clientCalendarName = clientCalendarName;
        this.dayOfMonth = dayOfMonth;
    }

    public Session(String clientName, String clientCalendarName, int amount, int dayOfMonth, String comment, boolean isPaid) {
        this.clientName = clientName;
        this.clientCalendarName = clientCalendarName;
        this.amount = amount;
        this.dayOfMonth = dayOfMonth;
        this.comment = comment;
        this.isPaid = isPaid;
    }

    public Map<String, Object> toSessionsMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("clientName", clientName);
        result.put("clientCalendarName", clientCalendarName);
        result.put("amount", amount);
        result.put("dayOfMonth", dayOfMonth);
        result.put("comment", comment);
        result.put("isPaid", isPaid);

        return result;
    }




}
