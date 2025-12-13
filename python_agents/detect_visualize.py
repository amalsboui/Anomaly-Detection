import sqlite3
import matplotlib.pyplot as plt

# Thresholds
CPU_THRESHOLD = 80
BW_THRESHOLD = 200000

# Path to DB
DB_PATH = '/home/amalsboui/Desktop/Projects/AnomalyDetection/server/anomalies.db'

# Connect to DB
conn = sqlite3.connect(DB_PATH)
cursor = conn.cursor()
cursor.execute("SELECT host, cpu, bandwidth, timestamp FROM anomalies")
rows = cursor.fetchall()
conn.close()

# Prepare data per host
hosts = {}
for host, cpu, bw, ts in rows:
    if host not in hosts:
        hosts[host] = {'timestamps': [], 'cpu': [], 'bw': []}
    hosts[host]['timestamps'].append(ts)
    hosts[host]['cpu'].append(cpu)
    hosts[host]['bw'].append(bw)

# Plot CPU separately
plt.figure(figsize=(12, 6))
for host, data in hosts.items():
    plt.plot(data['timestamps'], data['cpu'], label=f'{host} CPU')
    # Detect CPU anomalies
    anomalies_x = [data['timestamps'][i] for i, v in enumerate(data['cpu']) if v > CPU_THRESHOLD]
    anomalies_y = [v for i, v in enumerate(data['cpu']) if v > CPU_THRESHOLD]
    plt.scatter(anomalies_x, anomalies_y, color='red', zorder=5)
plt.title('CPU Usage per Host with Anomalies')
plt.xlabel('Timestamp')
plt.ylabel('CPU %')
plt.xticks(rotation=45)
plt.legend()
plt.tight_layout()
plt.show()

# Plot Bandwidth separately
plt.figure(figsize=(12, 6))
for host, data in hosts.items():
    timestamps = data['timestamps']
    cpu_values = data['cpu']
    bw_values = data['bandwidth']

   # Detect BW anomalies
    anomalies_x = [data['timestamps'][i] for i, v in enumerate(data['bw']) if v > BW_THRESHOLD]
    anomalies_y = [v for i, v in enumerate(data['bw']) if v > BW_THRESHOLD]
    plt.scatter(anomalies_x, anomalies_y, color='orange', zorder=5)
plt.title('Bandwidth per Host with Anomalies')
plt.xlabel('Timestamp')
plt.ylabel('Bandwidth')
plt.xticks(rotation=45)
plt.legend()
plt.tight_layout()
plt.show()
