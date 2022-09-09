package com.ButlrObjects.ButlrSensor;

import com.ButlrObjects.ButlrGateway.Gateway;
import com.ButlrObjects.ButlrHive.Hive;
import com.ButlrObjects.ButlrRoom.Room;
import com.ButlrObjects.ButlrSpace.Space;
import org.json.JSONArray;
import org.json.JSONObject;

public class Sensor {

    float[] coordinates;
    int height;
    Hive hive;
    String id;
    String mac_address;
    String mode;
    float[] orientation;
    Room room;
    Space space;

  // optional
    int[] activeHours;
    Gateway gateway;
    boolean isBatteryLow;
    String lastBatteryChange;
    int  remainingBatteryDays;
    int remainingBatteryPercent;

  // tracking
    int numIn = 0;
    int numOut = 0;


    public Sensor() {
        this.coordinates = new float[2];
        this.height = 0;
        this.hive = null;
        this.id = "sensor_xxxxxxxxxxxxxxxxxxxxxxxxxxx";
        this.mac_address = "00-17-0d-00-00-xx-xx-xx";
        this.mode = "activity";
        this.orientation = new float[3];
        this.room = null;
        this.space = null;
    }

    public Sensor( String sensor_id, Hive hive, Room room, Space space, JSONObject requestBody ) {
        this.id = sensor_id;
        this.hive = hive;
        this.room = room;
        this.space = space;

        if ( ! requestBody.isNull("height") ) this.height = requestBody.optInt("height");

        this.coordinates = new float[2];
        if ( ! requestBody.isNull("coordinates") ) this.coordinates[0] = requestBody.optJSONArray("coordinates").optInt(0);
        if ( ! requestBody.isNull("coordinates") ) this.coordinates[1] = requestBody.optJSONArray("coordinates").optInt(1);

        if ( ! requestBody.isNull("mac_address") ) this.mac_address = requestBody.optString("mac_address");

        if ( ! requestBody.isNull("mode") ) this.mode = requestBody.optString("mode");

        this.orientation = new float[3];
        if ( ! requestBody.isNull("orientation") ) this.orientation[0] = requestBody.optJSONArray("orientation").optInt(0);
        if ( ! requestBody.isNull("orientation") ) this.orientation[1] = requestBody.optJSONArray("orientation").optInt(1);
        if ( ! requestBody.isNull("orientation") ) this.orientation[2] = requestBody.optJSONArray("orientation").optInt(2);

        this.activeHours = new int[7];
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[0] = requestBody.optJSONArray("activeHours").optInt(0);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[1] = requestBody.optJSONArray("activeHours").optInt(1);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[2] = requestBody.optJSONArray("activeHours").optInt(2);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[3] = requestBody.optJSONArray("activeHours").optInt(3);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[4] = requestBody.optJSONArray("activeHours").optInt(4);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[5] = requestBody.optJSONArray("activeHours").optInt(5);
        if ( ! requestBody.isNull("activeHours") ) this.activeHours[6] = requestBody.optJSONArray("activeHours").optInt(6);

        if ( ! requestBody.isNull("isBatteryLow") ) this.isBatteryLow = requestBody.optBoolean("isBatteryLow");

        if ( ! requestBody.isNull("lastBatteryChange") ) this.lastBatteryChange = requestBody.optString("lastBatteryChange");

        if ( ! requestBody.isNull("remainingBatteryDays") ) this.remainingBatteryDays = requestBody.optInt("remainingBatteryDays");

        if ( ! requestBody.isNull("remainingBatteryPercent") ) this.remainingBatteryPercent = requestBody.optInt("remainingBatteryPercent");
    }

    public void delete() {
        hive.removeSensor( id );
        room.removeSensor( id );
        space.removeSensor( id );
    }

    public void clearSpace() {
        space = null;
    }

    public void setSpace( Space space ) { this.space = space; }

    public void clearRoom() {
        room = null;
    }

    public void clearHive() {
        hive = null;
    }

    public void trafficReset() {
        trafficInReset();
        trafficOutReset();
        room.resetOccupancy();
    }

    public void trafficInReset() {
        numIn = 0;
    }

    public void trafficOutReset() {
        numOut = 0;
    }

    public void incrementTrafficIn() {
        numIn++;
        room.incrementOccupancy( 1 );
    }

    public void incrementTrafficIn( int amount ) {
        numIn += amount;
        room.incrementOccupancy( amount );
    }

    public void incrementTrafficOut() {
        numOut++;
        room.decrementOccupancy( 1 );
    }

    public void incrementTrafficOut( int amount ) {
        numOut += amount;
        room.decrementOccupancy( amount );
    }

    public JSONObject getTrafficInJson() {
        return new JSONObject()
                .put("start", "")    // need to revisit this to add start/stop time for traffic queries
                .put("stop", "")
                .put("device_id", mac_address)
                .put("hive_id", hive.getId())
                .put("field", "in")
                .put("value", numIn);
    }

    public JSONObject getTrafficOutJson() {
        return new JSONObject()
                .put("start", "")    // need to revisit this to add start/stop time for traffic queries
                .put("stop", "")
                .put("device_id", mac_address)
                .put("hive_id", hive.getId())
                .put("field", "out")
                .put("value", numOut);
    }

    public JSONObject getDetailsJson() {
        JSONArray orientationArray = new JSONArray();
        for ( float num : orientation ) orientationArray.put( num );

        return new JSONObject()
                .put("id", id)
                .put("mac_address", mac_address)
                .put("mode", mode)
                .put("hive", (hive != null ? hive.getDetailsJson() : new JSONObject()))
                .put("space", (space != null ? space.getDetailsJson() : new JSONObject()))
                .put("room", (room != null ? room.getDetailsJson() : new JSONObject()))
                .put("orientation", orientationArray)
                .put("height", height);
    }
}
