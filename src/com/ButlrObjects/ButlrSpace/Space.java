package com.ButlrObjects.ButlrSpace;

import com.ButlrObjects.ButlrGateway.Gateway;
import com.ButlrObjects.ButlrHive.Hive;
import com.ButlrObjects.ButlrRoom.Room;
import com.ButlrObjects.ButlrSensor.Sensor;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Space {

    String address;
    int area;
    int capacity;
    ArrayList<Gateway> gateways = new ArrayList<>();
    Map<String, Hive> hives = new HashMap<>();
    String id;
    String installation_date;
    int mid_capacity;
    String name;
    Map<String, Room> rooms = new HashMap<>();
    Map<String, Sensor> sensors = new HashMap<>();
    String timezone;

  // tracking
    int occupancy = 0;


    public Space() {
        this.address = "";
        this.area = 0;
        this.capacity = 0;
        this.gateways = new ArrayList<>();
        this.id = "space_xxxxxxxxxxxxxxxxxxxxxxxxxxx";
        this.installation_date = "";
        this.mid_capacity = 0;
        this.name = "";
        this.timezone = "";
    }

    public Space( String id, JSONObject requestBody ) {
        this.id = id;

        if ( ! requestBody.isNull("address") ) this.address = requestBody.optString("address");
        if ( ! requestBody.isNull("area") ) this.area = requestBody.optInt("area");
        if ( ! requestBody.isNull("capacity") ) this.capacity = requestBody.optInt("capacity");
        if ( ! requestBody.isNull("installation_date") ) this.installation_date = requestBody.optString("installation_date");
        if ( ! requestBody.isNull("mid_capacity") ) this.mid_capacity = requestBody.optInt("mid_capacity");
        if ( ! requestBody.isNull("name") ) this.name = requestBody.optString("name");
        if ( ! requestBody.isNull("timezone") ) this.timezone = requestBody.optString("timezone");
    }

    public void delete() {
        hives.forEach( (k,v) -> v.clearSpace() );
        rooms.forEach( (k,v) -> v.clearSpace() );
        sensors.forEach( (k,v) -> v.clearSpace() );
    }

    public void removeRoom( String room_id ) {
        rooms.remove( room_id );
    }

    public void removeHive( String hive_id ) {
        hives.remove( hive_id );
    }

    public void removeSensor( String sensor_id ) {
        sensors.remove( sensor_id );
    }

    public void incrementOccupancy( int amount ) { occupancy += amount; }

    public void decrementOccupancy( int amount ) { occupancy -= amount; }

    public void resetOccupancy() { occupancy = 0; }

    public String getId() { return this.id; }

    public JSONObject update( JSONObject requestBody, Map<String, Hive> hiveMap, Map<String, Room> roomMap, Map<String, Sensor> sensorMap ) {
        // should this method remove existing hives, rooms, sensors? or just add the new ones?
        // for now, it will replace all existing hives/rooms/sensors

        // parse json
        // should probably parse the entire json and add any error messages to errors JSONArray
        // for now we just return bad request if any variable is invalid or missing, proper error checking to be implemented in the future
        String address = requestBody.optString( "address" );
        if ( address.isEmpty() ) return new JSONObject().put("message", "Error updating space, request body did not include valid address.");

        int area = requestBody.optInt( "area" );
        int capacity = requestBody.optInt( "capacity" );
        // skip over gateways for now

        String id = requestBody.optString( "id" );
        if ( id.isEmpty() ) return new JSONObject().put("message", "Error updating space, request body did not include id or it was empty.");

        String installation_date = requestBody.optString( "installation_date" );
        int mid_capacity = requestBody.optInt( "mid_capacity" );

        String name = requestBody.optString( "name" );
        if ( name.isEmpty() ) return new JSONObject().put("message", "Error updating space, request body did not include name or it was empty.");

        String timezone = requestBody.optString( "timezone" );

        // update variables from json request body
        this.address = address;
        this.area = area;
        this.capacity = capacity;
        this.id = id;
        this.installation_date = installation_date;
        this.mid_capacity = mid_capacity;
        this.name = name;
        this.timezone = timezone;


        // replace hives
        hives.forEach( (k,v) -> v.clearSpace() ); // remove current hives
        hiveMap.forEach( (k,v) -> v.setSpace( this ) );
        hives = hiveMap;

        // replace rooms
        rooms.forEach( (k,v) -> v.clearSpace() );
        roomMap.forEach( (k,v) -> v.setSpace( this ) );
        rooms = roomMap;

        // replace sensors
        sensors.forEach( (k,v) -> v.clearSpace() );
        sensorMap.forEach( (k,v) -> v.setSpace( this ) );
        sensors = sensorMap;

        // prepare json response
        JSONArray hiveArray = new JSONArray();
        hives.forEach( (k,v) -> hiveArray.put( v.getInfoJson() ) );

        JSONArray roomArray = new JSONArray();
        rooms.forEach( (k,v) -> roomArray.put( v.getDetailsJson() ) );

        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) -> sensorArray.put( v.getDetailsJson() ) );

        return new JSONObject()
                .put("address", this.address)
                .put("area", this.area)
                .put("capacity", this.capacity)
                // skip gateways for now
                .put("hives", hiveArray)
                .put("id", this.id)
                .put("installation_date", this.installation_date)
                .put("mid_capacity", this.mid_capacity)
                .put("name", this.name)
                .put("rooms", roomArray)
                .put("sensors", sensorArray)
                .put("timezone", timezone);
    }

    public JSONObject getDetailsJson() {
        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) ->  sensorArray.put( v.getDetailsJson() ) );

        JSONArray hiveArray = new JSONArray();
        hives.forEach( (k,v) -> hiveArray.put( v.getDetailsJson() ) );

        JSONArray roomArray = new JSONArray();
        rooms.forEach( (k,v) -> roomArray.put( v.getDetailsJson() ) );

        JSONArray gatewayArray = new JSONArray();
        for ( Gateway gateway : gateways ) gatewayArray.put( gateway.getDetailsJson() );

        return new JSONObject()
                .put("id", id)
                .put("name", name)
                .put("address", address)
                .put("capacity", capacity)
                .put("mid_capacity", mid_capacity)
                .put("area", area)
                .put("timezone", timezone)
                .put("installation_date", installation_date)
                .put("sensors", sensorArray)
                .put("hives", hiveArray)
                .put("rooms", roomArray)
                .put("gateways", gatewayArray);
    }

    public JSONObject getCreateJson() {
        return new JSONObject().put("name", name).put("id", id);
    }

    public JSONObject getInfoJson() {
        return new JSONObject().put("name", name).put("id", id);
    }
}
