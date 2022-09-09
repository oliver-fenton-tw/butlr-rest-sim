import com.ButlrObjects.ButlrSpace.*;
import com.ButlrObjects.ButlrGateway.*;
import com.ButlrObjects.ButlrHive.*;
import com.ButlrObjects.ButlrSensor.*;
import com.ButlrObjects.ButlrMQTT.*;
import com.ButlrObjects.ButlrRoom.*;
import com.ButlrController.*;
import org.json.JSONArray;
import org.json.JSONObject;


public class test {
    public static void main(String[] args) {
      /*
        Hive hive = new Hive();
        Space space = new Space();
        Gateway gateway = new Gateway();
        Sensor sensor = new Sensor();
        Mqtt mqtt = new Mqtt();
        Room room = new Room();

        System.out.println( hive.getDetailsJson() );
        System.out.println( space.getDetailsJson() );
        System.out.println( gateway.getDetailsJson() );
        System.out.println( sensor.getDetailsJson() );
        System.out.println( mqtt.getDetailsJson() );
        System.out.println( room.getDetailsJson() );
     */

        ButlrRequestController controller = new ButlrRequestController();

        System.out.println( controller.createSpace("space_0001", new JSONObject().put("name", "Test Space")));

        System.out.println( controller.createRoom( "room_0001", "space_0001", new JSONArray().put(new JSONObject().put("name", "Test Room"))));

        controller.createHive( "hive_0001", new JSONObject() );
        System.out.println( controller.listHives() );

        controller.createSensor("sensor_0001", "hive_0001", "room_0001", "space_0001", new JSONObject());
        System.out.println( controller.listSensors() );

        System.out.println( controller.listRooms() );
        System.out.println( controller.listSpaces() );


        System.out.println( controller.getTraffic() );
        System.out.println( controller.getPresence() );

        controller.incrementSensorInTraffic("sensor_0001", 7);
        System.out.println( controller.getTraffic() );
        System.out.println( controller.getPresence() );

        controller.incrementSensorOutTraffic("sensor_0001", 3);
        System.out.println( controller.getTraffic() );
        System.out.println( controller.getPresence() );

    }
}
