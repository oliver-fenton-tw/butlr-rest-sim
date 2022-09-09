package com.ButlrController;

import com.ButlrObjects.ButlrHive.*;
import com.ButlrObjects.ButlrRoom.*;
import com.ButlrObjects.ButlrSensor.*;
import com.ButlrObjects.ButlrSpace.*;
import com.ButlrController.RandomStringGenerator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class ButlrRequestController {
    // Butlr controller manages the spaces, rooms, and devices; and coordinates the relationship among them
    Map<String, Space> spaces = new HashMap<>();
    Map<String, Room> rooms = new HashMap<>();
    Map<String, Hive> hives = new HashMap<>();
    Map<String, Sensor> sensors = new HashMap<>();

    // used to generate random alphanumeric strings for device ids
    RandomStringGenerator random = new RandomStringGenerator();


    public JSONObject createSpace( String space_id, JSONObject requestBody ) {
        Space space = new Space( space_id, requestBody );
        spaces.put( space_id, space );

        return space.getCreateJson();
    }

    public JSONObject listSpaces() {
        JSONArray spaceList = new JSONArray();
        spaces.forEach( (k,v) -> spaceList.put( v.getDetailsJson() ) );
        return new JSONObject().put("data", spaceList);
    }

    // returns true if space exists and is successfully deleted, and false otherwise
    public boolean deleteSpace( String space_id ) {
        if ( spaces.containsKey( space_id ) ) {
            // call space delete method to remove space reference from any hives, rooms, or sensors
            spaces.get( space_id ).delete();
            spaces.remove( space_id );
            return true;
        }
        return false;
    }


    /*
        updateSpace( String space_id, JSONObject requestBody )
        Update a space or add a sensor
        returns response as a JSONObject

        Required values in request body:
            address - string non-empty
            area - number
            capacity - number
            gateways - Array of objects non-empty unique
            hives - Array of objects non-empty unique
            id - string non-empty
            installation_date - string
            mid_capacity - number
            name - string non-empty
            rooms - Array of objects non-empty unique
            sensors - Array of objects non-empty unique
            timezone - string non-empty
     */
    public JSONObject updateSpace( String space_id, JSONObject requestBody ) {
        if ( spaces.containsKey( space_id ) ) { // space exists
            // prepare map of hives to add to space
            Map<String, Hive> hiveMap = new HashMap<>();
            if ( ! requestBody.isNull( "hives" ) ) {
                JSONArray hiveArray = requestBody.getJSONArray( "hives" );
                for ( int i = 0; i < hiveArray.length(); i++ ) {
                    String hive_id = hiveArray.getJSONObject( i ).optString( "id" ); // returns id, or empty string if no mapping exists
                    if ( hive_id.isEmpty() ) return new JSONObject().put("message", "Error updating space: hive id not provided.");
                    if ( ! hives.containsKey( hive_id ) ) return new JSONObject().put("message", "Error updating space: no hive with id " + hive_id + " exists.");
                    hiveMap.put( hive_id, hives.get( hive_id ) );
                }
            } else { // bad request, request body must include array of hives
                return new JSONObject().put("message", "Error updating space: request body did not include an array of hives.");
            }

            // prepare map of rooms to add to space
            Map<String, Room> roomMap = new HashMap<>();
            if ( ! requestBody.isNull( "rooms" ) ) {
                JSONArray roomArray = requestBody.getJSONArray( "rooms" );
                for ( int i = 0; i < roomArray.length(); i++ ) {
                    String room_id = roomArray.getJSONObject( i ).optString( "id" ); // returns id, or empty string if no mapping exists
                    if ( room_id.isEmpty() ) return new JSONObject().put("message", "Error updating space: room id not provided.");
                    if ( ! rooms.containsKey( room_id ) ) return new JSONObject().put("message", "Error updating space: no room with id " + room_id + " exists.");  // room does not exist, should it be created instead of aborting?
                    // does the room need to exist already, or can this request also create a new room?
                    roomMap.put( room_id, rooms.get( room_id ) );
                }
            } else { // bad request, request body must include array of rooms
                return new JSONObject().put("message", "Error updating space: request body did not include an array of rooms.");
            }

            // prepare map of sensors to add to space
            Map<String, Sensor> sensorMap = new HashMap<>();
            if ( ! requestBody.isNull( "sensors" ) ) {
                JSONArray sensorArray = requestBody.getJSONArray( "sensors" );
                for ( int i = 0; i < sensorArray.length(); i++ ) {
                    String sensor_id = sensorArray.getJSONObject( i ).optString( "id" ); // returns id, or empty string if no mapping exists
                    if ( sensor_id.isEmpty() ) return new JSONObject().put("message", "Error updating space: sensor id not provided.");
                    if ( ! sensors.containsKey( sensor_id ) ) return new JSONObject().put("message", "Error updating space: no sensor with id " + sensor_id + " exists.");  // sensor does not exist, should it be created instead of aborting?
                    // does the sensor need to exist already, or can this request also create a new sensor?
                    sensorMap.put( sensor_id, sensors.get( sensor_id ) );
                }
            } else { // bad request, request body must include array of sensors
                return new JSONObject().put("message", "Error updating space: request body did not include an array of sensors.");
            }

            // should verify request body beforehand to avoid any errors after changing the space id here
            String new_space_id = requestBody.optString( "id" );
            if ( new_space_id.isEmpty() ) return new JSONObject().put("message", "Error updating space: id in request body is invalid or missing.");
            // update space id in map
            spaces.put( new_space_id, spaces.get( space_id ) );
            spaces.remove( space_id );

            // call space update method, pass map of hives, rooms, and sensors as parameters
            return spaces.get( space_id ).update( requestBody, hiveMap, roomMap, sensorMap );

        } else { // space does not exist
            // Bad Request
            return new JSONObject().put("message", "Error updating space: no space with id " + space_id + " exists.");
        }
    }




    public JSONObject createRoom( String room_id, String space_id, JSONArray requestBody ) {
        JSONObject request = null;
        if ( ! requestBody.isNull(0) ) {
            request = requestBody.getJSONObject(0);
        } else {
            request = new JSONObject();
        }

        Space space = null;
        try {
            space = spaces.get( space_id );
            if ( space == null  ) throw new RuntimeException("Error creating room, a space with id " + space_id + " does not exist.");
        } catch ( Exception e ) {
            e.printStackTrace();
            //throw e;  allow creating a room with space = null ?
        }

        Room room = new Room( room_id, space, request );
        rooms.put( room_id, room );

        return room.getCreateJson();
    }

    public JSONObject listRooms() {
        JSONArray roomList = new JSONArray();
        rooms.forEach( (k,v) -> roomList.put( v.getDetailsJson() ) );
        return new JSONObject().put( "data", roomList );
    }

    public boolean deleteRoom( String room_id ) {
        if ( rooms.containsKey( room_id ) ) {
            // call room delete method to remove room reference from any sensors or space
            rooms.get( room_id ).delete();
            rooms.remove( room_id );
            return true;
        }
        return false;
    }

    public JSONObject updateRoom( String room_id, String space_id, JSONObject requestBody ) {
        boolean errorFlag = false;
        JSONArray errors = new JSONArray();

        if ( ! rooms.containsKey( room_id ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "room_id").put("message", "Error updating room, a room with id " + room_id + " does not exist."));
        }

        // verify json request body include required variables: color, coordinates, height, name, type
        if ( requestBody.isNull( "color" ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "color").put("message", "Error updating room, color in request body is missing."));
        }
        JSONArray colorArray = requestBody.optJSONArray("color");
        if ( colorArray == null || colorArray.length() < 3 || colorArray.length() > 4 ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "color").put("message", "Error updating room, color array in request body is invalid."));
        } // color

        if ( requestBody.isNull( "coordinates" ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinates array in request body is missing."));
        }
        JSONObject coordinates = requestBody.optJSONArray( "coordinates" ).getJSONObject( 0 );
        if ( coordinates == null ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinates in request body have invalid format."));
        } else {
            if (coordinates.isNull("0") || coordinates.isNull("1") || coordinates.isNull("2") || coordinates.isNull("3")) {
                errorFlag = true;
                errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinates in request body are invalid."));
            } else {
                if ( coordinates.optJSONArray("0") == null ) {
                    errorFlag = true;
                    errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinate 0 in request body is invalid."));
                }
                if ( coordinates.optJSONArray("1") == null ) {
                    errorFlag = true;
                    errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinate 1 in request body is invalid."));
                }
                if ( coordinates.optJSONArray("2") == null ) {
                    errorFlag = true;
                    errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinate 2 in request body is invalid."));
                }
                if ( coordinates.optJSONArray("3") == null ) {
                    errorFlag = true;
                    errors.put(new JSONObject().put("field", "coordinates").put("message", "Error updating room, coordinate 3 in request body is invalid."));
                }
            }
        } // coordinates

        if ( requestBody.isNull( "height" ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "height").put("message", "Error updating room, height in request body is missing."));
        } // height

        if ( requestBody.isNull( "name" ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "name").put("message", "Error updating room, name in request body is missing."));
        } // name

        if ( requestBody.isNull( "type" ) ) {
            errorFlag = true;
            errors.put(new JSONObject().put("field", "type").put("message", "Error updating room, type in request body is missing."));
        } // type

        if ( ! errorFlag ) {
            return rooms.get( room_id ).update( requestBody );
        } else {
            return new JSONObject().put("errors", errors).put("message", "Invalid request");
        }
    } // updateRoom()



    // this is not a butlr method, but is necessary to facilitate simulation
    public void createHive( String hive_id, JSONObject requestBody ) {
        Hive hive = new Hive( hive_id, requestBody );
        hives.put( hive_id, hive );
    } // createHive()

    public JSONObject listHives() {
        JSONArray hiveList = new JSONArray();
        hives.forEach( (k,v) -> hiveList.put( v.getInfoJson() ) );
        return new JSONObject().put( "data", hiveList );
    } // listHives()

    public boolean deleteHive( String hive_id ) {
        if ( hives.containsKey( hive_id ) ) {
            // call hive delete method to remove hive reference from any sensors or space
            hives.get( hive_id ).delete();
            hives.remove( hive_id );
            return true;
        }
        return false;
    } // deleteHive()

    /*
    public JSONObject updateHive( String hive_id ) {
        // not sure what this method should do, it is unclear based on the Butlr REST API documentation
    }
    */



    // this is not a butlr method, but is necessary to facilitate simulation
    public void createSensor( String sensor_id, String hive_id, String room_id, String space_id, JSONObject requestBody ) {
        Hive hive = null;
        try {
            hive = hives.get( hive_id );
            if ( hive == null  ) throw new RuntimeException("Error creating sensor, a hive with id " + hive_id + " does not exist.");
        } catch ( Exception e ) {
            e.printStackTrace();
            //throw e;  allow creating a sensor with hive = null ?
        }

        Room room = null;
        try {
            room = rooms.get( room_id );
            if ( room == null  ) throw new RuntimeException("Error creating sensor, a room with id " + room_id + " does not exist.");
        } catch ( Exception e ) {
            e.printStackTrace();
            //throw e;  allow creating a sensor with room = null ?
        }

        Space space = null;
        try {
            space = spaces.get( space_id );
            if ( space == null  ) throw new RuntimeException("Error creating sensor, a space with id " + space_id + " does not exist.");
        } catch ( Exception e ) {
            e.printStackTrace();
            //throw e;  allow creating a sensor with space = null ?
        }

        Sensor sensor = new Sensor( sensor_id, hive, room, space, requestBody );
        sensors.put( sensor_id, sensor );
    }

    public JSONObject listSensors() {
        JSONArray sensorList = new JSONArray();
        sensors.forEach( (k,v) -> sensorList.put( v.getDetailsJson() ) );
        return new JSONObject().put( "data", sensorList );
    }

    public boolean deleteSensor( String sensor_id ) {
        if ( sensors.containsKey( sensor_id ) ) {
            // call sensor delete method to remove sensor reference from hive, room, space
            sensors.get( sensor_id ).delete();
            sensors.remove( sensor_id );
            return true;
        }
        return false;
    }

    /*
    public JSONObject updateSensor( String sensor_id ) {
        // not sure what this method should do, it is unclear based on the Butlr REST API documentation
    }
    */

    public JSONObject getTraffic() {
        JSONArray trafficInArray = new JSONArray();
        sensors.forEach( (k,v) -> trafficInArray.put( v.getTrafficInJson() ) );

        JSONArray trafficOutArray = new JSONArray();
        sensors.forEach( (k,v) -> trafficOutArray.put( v.getTrafficOutJson() ) );

        return new JSONObject().put("data", new JSONObject()
                    .put("in", trafficInArray)
                    .put("out", trafficOutArray));
    }

    public JSONArray getPresence() {
        // unclear which butlr object the occupancy is associated with
        // I assume it would be space or room, but the response json for this includes sensor mac_address and device_id
        // I'm not sure how the mac_address, device_id, and hive_serial relate to this so for now they are ignored and occupancy is associated with rooms
        // each space also has its own occupancy but query for this is not implemented
        // time also ignored for now

        JSONArray presenceResponseJson = new JSONArray();
        rooms.forEach( (k,v) -> presenceResponseJson.put( v.getPresenceJson() ) );
        return presenceResponseJson;
    }

    public void incrementSensorInTraffic( String sensor_id, int amount ) {
        if ( ! sensors.containsKey( sensor_id ) ) throw new RuntimeException("Error incrementing sensor in traffic: no sensor with id " + sensor_id + " exists.");
        sensors.get( sensor_id ).incrementTrafficIn( amount );
    }

    public void incrementSensorOutTraffic( String sensor_id, int amount ) {
        if ( ! sensors.containsKey( sensor_id ) ) throw new RuntimeException("Error incrementing sensor out traffic: no sensor with id " + sensor_id + " exists.");
        sensors.get( sensor_id ).incrementTrafficOut( amount );
    }
}
