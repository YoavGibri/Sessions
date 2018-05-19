package com.yoavgibri.myincome.Models;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Yoav on 03/08/16.
 */
public class Client implements Comparable<Client> {
    //    public int client;
    public String clientName;
    public int totalAmount;
    public int totalCount;


    public Client(String clientName) {
        this.clientName = clientName;
    }

    public Client(String clientName, int totalAmount, int totalCount) {
        this.clientName = clientName;
        this.totalAmount = totalAmount;
        this.totalCount = totalCount;
    }

    public Client() {
    }

    public String toString() {
        return clientName;
    }

    public static Comparator<? super Client> amountComparator = new Comparator<Client>() {
        @Override
        public int compare(Client c1, Client c2) {
            return c1.compareTo(c2);
        }
    };

    @Override
    public int compareTo(@NonNull Client o) {
        if (totalAmount == o.totalAmount) return 0;
        return totalAmount > o.totalAmount ? 1 : -1;
    }
}
