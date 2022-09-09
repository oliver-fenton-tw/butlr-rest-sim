package com.ButlrObjects.ButlrRoom;

import com.ButlrObjects.ButlrSensor.Sensor;
import com.ButlrObjects.ButlrSpace.Space;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Room {

    ArrayList<float[]> coordinates;
    String id;
    String name;
    String type;
    Map<String, Sensor> sensors = new HashMap<>();
    int area;
    int capacity;
    int[] color;
    int door_line;
    int in_direction;
    int maxCapacity;
    int mid_capacity;
    int parallel_to_door;
    int sensitivity;
    int height;
    Space space;

  // tracking
    int occupancy = 0;


    public Room() {
        this.coordinates = new ArrayList<>();
        this.id = "room_xxxxxxxxxxxxxxxxxxxxxxxxxxx";
        this.type = "type_xxxxxxxxxxxxxxxxxxxxxxxxxxx";
    }

    public Room( String id, Space space, JSONObject requestBody ) {
        this.color = new int[4];
        if ( ! requestBody.isNull("color") ) {
            JSONArray colorArray = requestBody.getJSONArray("color");
            for (int i = 0; i < 4 && i < colorArray.length(); i++) {
                color[i] = colorArray.optInt(i);
            }
        }

        this.coordinates = new ArrayList<>();
        if ( ! requestBody.isNull("coordinates") ) {
            JSONArray coordinatesArray = requestBody.getJSONArray("coordinates");
            for (int i = 0; i < coordinatesArray.length(); i++) {
                float[] point = {coordinatesArray.getJSONArray(i).optInt(0), coordinatesArray.getJSONArray(i).optInt(1)};
                coordinates.add(point);
            }
        } else {
            coordinates.add(new float[2]);
            coordinates.add(new float[2]);
            coordinates.add(new float[2]);
            coordinates.add(new float[2]);
        }

        if ( ! requestBody.isNull("height") ) this.height = requestBody.optInt("height");
        else this.height = 0;

        if ( ! requestBody.isNull("name") ) this.name = requestBody.optString("name");
        else this.name = "default_room_name";

        if ( ! requestBody.isNull("type") ) this.type = requestBody.optString("type");
        else this.type = "default_room_type";

        this.id = id;
        this.space = space;
    }

    public void delete() {
        sensors.forEach( (k,v) -> v.clearRoom() );
        space.removeRoom( id );
    }

    public void removeSensor( String sensor_id ) {
        sensors.remove( sensor_id );
    }

    public void clearSpace() {
        this.space = null;
    }

    public void setSpace( Space space ) { this.space = space; }

    public void incrementOccupancy( int amount ) {
        occupancy += amount;
        space.incrementOccupancy( amount );
    }

    public void decrementOccupancy( int amount ) {
        occupancy -= amount;
        space.decrementOccupancy( amount );
    }

    public void resetOccupancy() {
        space.decrementOccupancy( occupancy );
        occupancy = 0;
    }

    public JSONObject update( JSONObject requestBody ) {
        // error checking is handled by caller (ButlrRequestController)
        // so we assume the request body is valid here

        JSONArray color = requestBody.getJSONArray( "color" );
        this.color[0] = color.optInt(0);
        this.color[1] = color.optInt(1);
        this.color[2] = color.optInt(2);
        this.color[3] = color.optInt(3, 255); // color array can have 3 or 4 values, 4th defaults to 255 when only 3 are provided

        JSONObject coordinates = requestBody.getJSONArray( "coordinates" ).getJSONObject( 0 );
        this.coordinates.clear();
        // need to update this in the future to handle polygon rooms, for now we assume there are only 4 coordinates
        JSONArray point;
        point = coordinates.getJSONArray("0");
        this.coordinates.add( new float[]{point.optFloat(0), point.optFloat(1)});
        point = coordinates.getJSONArray("1");
        this.coordinates.add( new float[]{point.optFloat(0), point.optFloat(1)});
        point = coordinates.getJSONArray("2");
        this.coordinates.add( new float[]{point.optFloat(0), point.optFloat(1)});
        point = coordinates.getJSONArray("3");
        this.coordinates.add( new float[]{point.optFloat(0), point.optFloat(1)});

        this.height = requestBody.optInt("height");
        this.name = requestBody.optString("name");
        this.type = requestBody.optString("type");

        // prepare sensor json array for response
        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) -> sensorArray.put( v.getDetailsJson() ) );

        return new JSONObject()
                .put("area", this.area)
                .put("capacity", this.capacity)
                .put("color", requestBody.getJSONArray("color"))
                .put("coordinates", requestBody.getJSONArray("coordinates"))
                .put("id", this.id)
                .put("maxCapacity", this.maxCapacity)
                .put("mid_capacity", this.mid_capacity)
                .put("name", this.name)
                .put("sensors", sensorArray)
                .put("space", this.space.getInfoJson())
                .put("type", this.type);
    }

    public JSONObject getDetailsJson() {
        JSONArray coordinatesArray = new JSONArray();
        for ( float[] point : coordinates ) coordinatesArray.put( new JSONArray().put( point[0] ).put( point[1] ) );

        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) -> sensorArray.put( v.getDetailsJson() ) );

        return new JSONObject()
                .put("id", id)
                .put("type", type)
                .put("coordinates", coordinatesArray)
                .put("sensors", sensorArray);
    }

    public JSONObject getCreateJson() {
        JSONArray coordinatesArray = new JSONArray();
        for ( float[] point : coordinates ) coordinatesArray.put( new JSONArray().put( point[0] ).put( point[1] ) );

        JSONArray colorArray = new JSONArray();
        for ( int c : color ) colorArray.put( c );

        JSONArray sensorArray = new JSONArray();
        sensors.forEach( (k,v) -> sensorArray.put( v.getDetailsJson() ) );

        return new JSONObject()
                .put("coordinates", coordinatesArray)
                .put("id", id)
                .put("name", name)
                .put("type", type)
                .put("area", area)
                .put("capacity", capacity)
                .put("color", colorArray)
                .put("door_line", door_line)
                .put("in_direction", in_direction)
                .put("maxCapacity", maxCapacity)
                .put("mid_capacity", mid_capacity)
                .put("parallel_to_door", parallel_to_door)
                .put("sensitivity", sensitivity)
                .put("sensors", sensorArray)
                .put("space", space.getInfoJson())
                .put("maxCapacity", maxCapacity)
                .put("area", area)
                .put("capacity", capacity)
                .put("mid_capacity", mid_capacity)
                .put("door_line", door_line)
                .put("parallel_to_door", parallel_to_door)
                .put("in_direction", in_direction)
                .put("sensitivity", sensitivity);
    }

    public JSONObject getPresenceJson() {
        return new JSONObject()
                .put("occupancy", String.valueOf( occupancy ))
                .put("value", occupancy)
                .put("space_id", space.getId())
                .put("room_id", this.id);
    }
}
