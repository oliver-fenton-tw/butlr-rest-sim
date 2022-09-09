package com.ButlrObjects.ButlrHive;

import com.ButlrObjects.ButlrGateway.Gateway;
import com.ButlrObjects.ButlrMQTT.Mqtt;
import com.ButlrObjects.ButlrSensor.Sensor;
import com.ButlrObjects.ButlrSpace.Space;

import org.json.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Hive {

    ArrayList<Gateway> gateways = new ArrayList<>();
    String id;
    Mqtt mqtt = new Mqtt();
    Map<String, Sensor> sensors = new HashMap<>();
    String serial_number;
    Space space;


    public Hive() {
        this.gateways = new ArrayList<>();
        this.id = "";
        this.mqtt = new Mqtt();
        this.serial_number = serial_number;
        this.space = null;
    }

    public Hive( String id, JSONObject requestBody ) {
        this.id = id;

        // need to revisit this to parse json body and set other class variables
    }

    public void delete() {
        sensors.forEach( (k,v) -> v.clearHive() );
        space.removeHive( id );
    }

    public void clearSpace() {
        this.space = null;
    }

    public void setSpace( Space space ) { this.space = space; }

    public void removeSensor( String sensor_id ) {
        sensors.remove( sensor_id );
    }

    public String getId() { return this.id; }

    public JSONObject getDetailsJson() {

        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) -> sensorArray.put( v.getDetailsJson() ) );

        JSONArray gatewayArray = new JSONArray();
        for ( Gateway gateway : gateways ) gatewayArray.put( gateway.getDetailsJson() );

        return new JSONObject()
                .put("id", id)
                .put("serial_number", serial_number)
                .put("sensors", sensorArray)
                .put("gateways", gatewayArray)
                .put("space", (space != null ? space.getDetailsJson() : new JSONObject()))
                .put("mqtt", mqtt.getDetailsJson());
    }

    public JSONObject getInfoJson() {
        return new JSONObject()
                .put("id", id)
                .put("serial_number", serial_number)
                .put("num_sensors", sensors.size());
    }
}
