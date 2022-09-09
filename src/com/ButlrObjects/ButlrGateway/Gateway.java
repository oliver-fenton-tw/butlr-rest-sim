package com.ButlrObjects.ButlrGateway;

import org.json.JSONObject;

public class Gateway {

    String id;
    String mac_address;

    public Gateway() {
        id = "";
        mac_address = "";
    }

    public Gateway( String id, String mac_address ) {
        this.id = id;
        this.mac_address = mac_address;
    }

    public JSONObject getDetailsJson() {
        return new JSONObject()
                .put("id", id)
                .put("mac_address", mac_address);
    }
}
