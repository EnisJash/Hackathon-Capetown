package com.example.hackathon2022;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.*;
import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import org.json.JSONException;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        receiveFromMQTT();



    }
    public void receiveFromMQTT() {
        System.out.println("hallo");

        Thread t = new Thread() {
            @Override
            public void run() {
                try {

                    MQTT mqtt = new MQTT();

                    mqtt.setHost("broker.hivemq.com", 1883);
                    BlockingConnection connection = mqtt.blockingConnection();

                    System.err.println("vor connect receive");

                    connection.connect();

                    System.err.println("Nach connect receive");

                    // Subscribe to topics
                    String topic_key = "MQTTpublisherhackathon";
                    Topic[] topics = {new Topic(topic_key, QoS.EXACTLY_ONCE)};
                    connection.subscribe(topics);
                    System.out.println("nach connect und subscribe receive");


                    while (true) {

                        Message message = connection.receive();  //blockierendes Warten
                        System.out.println(message.getTopic());

                        final byte[] payload = message.getPayload();
                        final String payloadStr = new String(payload);
                        JSONObject receiveJSONObject = new JSONObject(payloadStr);

                        // process the message then:
                        message.ack();

                        //ToDo Verarbeitung entsprechend des Topics
                        if (message.getTopic().equals("MQTTpublisherhackathon")) {
                            try {
                                try{
                                    //String probability = receiveJSONObject.getString("psend");
                                    String temp  = receiveJSONObject.getString("temp");
                                    String hum = receiveJSONObject.getString("hum");

                                    runOnUiThread(new Thread() {
                                        //schreibe in GUI
                                        @Override
                                        public void run() {

                                            TextView txtStart = (TextView) findViewById(R.id.text1);
                                            TextView txtStart2= (TextView) findViewById(R.id.text3);
                                            ImageView img = (ImageView) findViewById(R.id.imageView2);

                                            if (Double.parseDouble(temp)<25){
                                                txtStart.setText("Temperature: "+temp+"°C \nStatus: Green");
                                            }else if(Double.parseDouble(temp)>=25 && Double.parseDouble(temp)<27){
                                                txtStart.setText("Temperature: "+temp+"°C \nStatus: Yellow");
                                            }else{
                                                txtStart.setText("Temperature: "+temp+"°C \nStatus: Red");
                                            }
                                            if (Double.parseDouble(hum)<60){
                                                txtStart2.setText("Humidity: "+hum+"% \nStatus: Green");
                                            }else if(Double.parseDouble(hum)>=60 && Double.parseDouble(hum)<70){
                                                txtStart2.setText("Humidity: "+hum+"% \nStatus: Yellow");
                                            }else{
                                                txtStart2.setText("Humidity: "+hum+"% \nStatus: Red");
                                            }
                                            if(Double.parseDouble(hum)>=70 || Double.parseDouble(temp)>=27){
                                                img.setImageResource(R.drawable.stayindead);
                                            }else if((Double.parseDouble(temp)>=25 && Double.parseDouble(temp)<27) || (Double.parseDouble(hum)>=60 && Double.parseDouble(hum)<70)){
                                                img.setImageResource(R.drawable.sortof);
                                            }else {
                                                img.setImageResource(R.drawable.stayin);
                                            }



                                        }
                                    });
                                }catch (JSONException e) {
                                    e.printStackTrace();
                                }




                            } catch(Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
        };
        t.start();
    }

}