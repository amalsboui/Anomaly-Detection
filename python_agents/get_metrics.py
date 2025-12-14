import psutil
import subprocess
import os
import json
import random

CPU_THRESHOLD = 80
BW_THRESHOLD = 60

HOST_TYPE = os.getenv("HOST_TYPE", "normal")

# Get CPU usage quickly
cpu = psutil.cpu_percent(interval=0.5)

# Get bandwidth
def get_bw():
    try:
        out = subprocess.check_output(
            ["iperf3", "-c", "10.0.0.4", "-t", "1", "-f", "k", "-J"],
            stderr=subprocess.DEVNULL,
            timeout=2
        ).decode()
        j = json.loads(out)
        bw = j['end']['sum_received']['bits_per_second'] / 8 / 1024
        return round(bw, 2)
    except:
        return round(random.uniform(10, 50), 2)

bw = get_bw()

# Force anomalies for testing
if HOST_TYPE == "cpu_stress":
    cpu = min(cpu + 50, 100)
elif HOST_TYPE == "bw_stress":
    bw = bw + 100
elif HOST_TYPE == "stress":
    cpu = min(cpu + 50, 100)
    bw = bw + 100

print(f"{cpu},{bw}")
