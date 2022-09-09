package com.ButlrObjects.ButlrMQTT;

import org.json.JSONObject;

public class Mqtt {
    Auth auth;
    String broker_address;
    float rc;
    Topics topics;

    class Auth {
        String username;
        String password;

        Auth() {
            this.username = "";
            this.password = "";
        }
        Auth( String username, String password ) {
            this.username = username;
            this.password = password;
        }

         JSONObject getDetailsJson() {
             return new JSONObject();
         }
    }

    class Topics {
        String cmd;
        String dtc;
        String raw;

        Topics() {
            this.cmd = "";
            this.dtc = "";
            this.raw = "";
        }

        Topics( String cmd, String dtc, String raw ) {
            this.cmd = cmd;
            this.dtc = dtc;
            this.raw = raw;
        }

        JSONObject getDetailsJson() {
            return new JSONObject()
                    .put("cmd", cmd)
                    .put("dtc", dtc)
                    .put("raw", raw);
        }
    }

    public Mqtt() {
        this.auth = new Auth();
        this.broker_address = "";
        this.rc = 0;
        this.topics = new Topics();
    }

    public Mqtt( String username,
                 String password,
                 String broker_address,
                 float rc,
                 String cmd,
                 String dtc,
                 String raw )
    {
        this.auth = new Auth( username, password );
        this.broker_address = broker_address;
        this.rc = rc;
        this.topics = new Topics( cmd, dtc, raw );
    }

    public JSONObject getDetailsJson() {
        return new JSONObject()
                .put("rc", rc)
                .put("topics", topics.getDetailsJson())
                .put("broker_address", broker_address)
                .put("auth", auth.getDetailsJson());
    }
}
