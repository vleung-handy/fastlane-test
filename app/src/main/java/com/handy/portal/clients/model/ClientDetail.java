package com.handy.portal.clients.model;

import com.google.gson.annotations.SerializedName;
import com.handy.portal.core.model.ErrorResponse;

/**
 * Created by sng on 7/24/17.
 * This class is used for the single client end point which returns client and stats object used
 * on the Client Details page
 */

public class ClientDetail extends ErrorResponse {
    @SerializedName("client")
    private Client mClient;

    @SerializedName("stats")
    private Stats mStats;

    public Client getClient() {
        return mClient;
    }

    public Stats getStats() {
        return mStats;
    }
}
