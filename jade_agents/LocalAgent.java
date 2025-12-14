import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.*;

public class LocalAgent extends Agent {
    private static final double CPU_THRESHOLD = 50;
    private static final double BW_THRESHOLD = 40;

    protected void setup() {
        System.out.println(getLocalName() + " started.");
        
        addBehaviour(new jade.core.behaviours.TickerBehaviour(this, 5000) {
            protected void onTick() {
                try {
                    // Run Python script to get CPU and bandwidth
                    Process p = Runtime.getRuntime().exec("python3 ../python_agents/get_metrics.py");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = reader.readLine(); // expected format: CPU,BW
                    reader.close();
                    p.waitFor();
                    
                    if(line == null || line.isEmpty()) {
                        System.out.println(getLocalName() + ": No metrics received");
                        return;
                    }
                    
                    String[] parts = line.trim().split(",");
                    double cpu = Double.parseDouble(parts[0]);
                    double bw = Double.parseDouble(parts[1]);
                    
                    // Anomaly is 0 if WITHIN threshold, 1 if EXCEEDS threshold
                    int isAnomaly = (cpu > CPU_THRESHOLD || bw > BW_THRESHOLD) ? 1 : 0;
                    
                    // Send metrics to server
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                    msg.addReceiver(new AID("server", AID.ISLOCALNAME));
                    msg.setContent(getLocalName() + "," + cpu + "," + bw + "," + isAnomaly);
                    send(msg);
                    
                    if(isAnomaly == 1){
                        System.out.println("🚨 ANOMALY sent: " + msg.getContent());
                    } else {
                        System.out.println("✓ Metrics sent: " + msg.getContent());
                    }
                    
                } catch (Exception e) {
                    System.out.println(getLocalName() + " ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
