package com.yoavgibri.myincome.Models;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Yoav on 05/08/16.
 */
public class SessionType implements Comparable<SessionType> {
    //    public int jobType;
    public String type;
    public int totalAmount;
    public int totalCount;

    public SessionType() {
    }

    public SessionType(String type) {
        this.type = type;
    }

    public SessionType(String type, int totalAmount, int totalCount) {
        this.type = type;
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return type;
    }

    public static Comparator<? super SessionType> amountComparator = new Comparator<SessionType>() {
        @Override
        public int compare(SessionType t1, SessionType t2) {
            return t1.compareTo(t2);
        }
    };

    @Override
    public int compareTo(@NonNull SessionType o) {
        if (totalAmount == o.totalAmount) return 0;
        return totalAmount > o.totalAmount ? 1 : -1;
    }
}
