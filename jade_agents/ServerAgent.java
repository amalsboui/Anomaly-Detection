import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import java.sql.*;
import java.io.*;

public class ServerAgent extends Agent {
    Connection conn;

    protected void setup() {
        System.out.println(getLocalName() + " started.");
        
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:../server/anomalies.db");
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS anomalies (" +
                "host TEXT, " +
                "cpu REAL, " +
                "bandwidth REAL, " +
                "timestamp TEXT, " +
                "is_anomaly INTEGER)"
            );
            stmt.close();
            
        } catch(Exception e){
            e.printStackTrace();
        }
        
        addBehaviour(new jade.core.behaviours.CyclicBehaviour(this) {
            public void action() {
                ACLMessage msg = receive();
                if(msg != null){
                    try {
                        String[] data = msg.getContent().split(",");
                        String host = data[0];
                        double cpu = Double.parseDouble(data[1]);
                        double bw = Double.parseDouble(data[2]);
                        int isAnomaly = Integer.parseInt(data[3]);
                        
                        String ts = java.time.LocalDateTime.now().toString();
                        
                        PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO anomalies (host,cpu,bandwidth,timestamp,is_anomaly) VALUES (?,?,?,?,?)"
                        );
                        ps.setString(1, host);
                        ps.setDouble(2, cpu);
                        ps.setDouble(3, bw);
                        ps.setString(4, ts);
                        ps.setInt(5, isAnomaly);
                        ps.executeUpdate();
                        ps.close();
                        
                        // Always print all logs
                        if(isAnomaly == 1){
                            System.out.println("🚨 ANOMALY detected on " + host + " | CPU=" + cpu + " | BW=" + bw);
                        } else {
                            System.out.println("Metrics received: " + host + " | CPU=" + cpu + " | BW=" + bw);
                        }
                        
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }
}
