import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.io.*;
import java.sql.*;

public class ServerAgent extends Agent {
    Connection conn;

    protected void setup() {
        System.out.println(getLocalName() + " started.");
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:../server/anomalies.db");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS anomalies (host TEXT, cpu REAL, bandwidth REAL, timestamp TEXT)");
        } catch(Exception e){ e.printStackTrace(); }

        addBehaviour(new jade.core.behaviours.CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    System.out.println("Alert received: " + msg.getContent());
                    try {
                        String[] data = msg.getContent().split(",");
                        String host = data[0];
                        double cpu = Double.parseDouble(data[1]);
                        double bw  = Double.parseDouble(data[2]);
                        String ts  = java.time.LocalDateTime.now().toString();

                        PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO anomalies (host,cpu,bandwidth,timestamp) VALUES (?,?,?,?)");
                        ps.setString(1, host);
                        ps.setDouble(2, cpu);
                        ps.setDouble(3, bw);
                        ps.setString(4, ts);
                        ps.executeUpdate();
                        ps.close();
                    } catch(Exception e){ e.printStackTrace(); }
                } else { block(); }
            }
        });
    }
}

