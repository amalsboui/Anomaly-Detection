import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import java.io.*;

public class LocalAgent extends Agent {
    protected void setup() {
        System.out.println(getLocalName() + " started.");

        addBehaviour(new jade.core.behaviours.TickerBehaviour(this, 5000) {
            protected void onTick() {
                try {
                    // Call Python script to get CPU/BW metrics
                    Process p = Runtime.getRuntime().exec("python3 ../python_agents/get_metrics.py");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line = reader.readLine();  // expected format: CPU,BW
                    String[] parts = line.split(",");
                    double cpu = Double.parseDouble(parts[0]);
                    double bw  = Double.parseDouble(parts[1]);

                    if(cpu > 80 || bw > 60){
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(new AID("server", AID.ISLOCALNAME));
                        msg.setContent(getLocalName() + "," + cpu + "," + bw);
                        send(msg);
                        System.out.println("Alert sent: " + msg.getContent());
                    }

                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

