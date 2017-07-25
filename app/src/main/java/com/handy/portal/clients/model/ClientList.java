package com.handy.portal.clients.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.model.ErrorResponse;

import java.util.ArrayList;

public class ClientList extends ErrorResponse {
    @SerializedName("clients")
    private ArrayList<Client> mClients;

    public ArrayList<Client> getClients() {
        return mClients;
    }
}
