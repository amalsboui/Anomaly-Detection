# Network Anomaly Detection Using Multi-Agent System

## Project Overview
This project implements a **network anomaly detection system** based on a **multi-agent architecture**.  
Local agents monitor system and network metrics (CPU usage and bandwidth) on different hosts and send the collected data to a central server agent.  
The server agent stores the data in a **SQLite database** and marks anomalies.  
Metrics and anomalies are visualized using **Grafana**.

The project uses:
- **JADE** for multi-agent communication
- **Mininet** for network emulation
- **Python** for metrics collection
- **SQLite** for data storage
- **Grafana** for visualization
- **psutil** Python library for CPU measurement
- **iperf3** for bandwidth measurement

---

## Architecture
- **LocalAgent (JADE)**  
  Runs on each host, periodically collects CPU and bandwidth metrics using Python scripts (`get_metrics.py`), and sends alerts if thresholds are exceeded.

- **ServerAgent (JADE)**  
  Receives metrics from LocalAgents, detects anomalies, and stores all metrics in `anomalies.db`. Anomalies are flagged in the `is_anomaly` column.

- **Mininet Topology**  
  Simulates a LAN with three hosts (`h1`, `h2`, `h3`) and one server (`h4`).

- **Grafana**  
  Connects to the SQLite database and visualizes CPU usage, bandwidth, and anomalies. Anomalies can be highlighted in red using the `is_anomaly` flag.

---

## Metrics and Anomalies
Monitored metrics:
- **CPU usage (%)**: collected via psutil
- **Bandwidth (KB/s)**: measure using iperf3

An anomaly is detected when:
- CPU usage > defined threshold (for testing, 50%)
- OR Bandwidth > defined threshold (for testing, 40 KB/s)

Each record stored in the database contains:
- Host name
- CPU usage
- Bandwidth
- Timestamp
- Anomaly flag (`is_anomaly`)

---

## File Descriptions

- **jade_agents/ServerAgent.java** → JADE agent that collects metrics and stores them in SQLite.
- **jade_agents/LocalAgent.java** → JADE agent that collects metrics and sends them to ServerAgent.
- **jade_agents/start_agents.sh** → Script to start all JADE agents.
- **python_agents/get_metrics.py** → Measures CPU and bandwidth:
  - CPU: `psutil`
  - Bandwidth: `iperf3`
  - Injects stress based on host type for testing anomalies
- **python_agents/cpu_stress.py** → Artificial CPU load for testing anomalies.
- **topology/topology.py** → Mininet topology definition.
- **server/anomalies.db** → SQLite database storing metrics and anomaly flags.

## Setup & Running Instructions

### 1. Start Mininet
```bash
sudo python3 topology/topology.py
```

Mininet CLI starts with hosts h1, h2, h3, and server h4.

### 2. Set Host Types

In Mininet CLI:
```bash
# h1 normal
mininet> h1 export HOST_TYPE=normal &

# h2 CPU stress
mininet> h2 export HOST_TYPE=cpu_stress &
mininet> h2 python3 python_agents/cpu_stress.py &

# h3 bandwidth stress
mininet> h3 export HOST_TYPE=bw_stress &
mininet> h3 iperf3 -c 10.0.0.4 -t 300 &
```

### 3. Start JADE Agents

From `jade_agents` directory:
```bash
./start_agents.sh
```

- ServerAgent listens for metrics
- LocalAgents send metrics every 5 seconds
- Anomalies appear in console logs as:
```
  🚨 ANOMALY detected on h2 | CPU=xx.x | BW=xx.x
```

### 4. Check SQLite Database
```bash
sqlite3 server/anomalies.db
```
```sql
sqlite> SELECT * FROM anomalies;
```

All metrics are logged; `is_anomaly = 1` indicates anomalies.

### 5. Visualize in Grafana

- Install SQLite plugin for Grafana
- Connect Grafana to `server/anomalies.db`
- Create panels:
  - CPU usage per host – line graph, highlight `is_anomaly = 1` in red
  - Bandwidth per host – line graph, highlight `is_anomaly = 1` in red
- Use filters and legends to differentiate hosts

## Testing & Example

- **h1**: normal host, metrics below thresholds → no anomaly
- **h2**: CPU stress → triggers CPU anomaly
- **h3**: bandwidth stress → triggers bandwidth anomaly

## Notes

- SQLite database is automatically created if missing.
- Adjust thresholds in `LocalAgent.java` or `get_metrics.py` for experiments.

