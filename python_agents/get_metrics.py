# python_agents/get_metrics.py
import psutil
import os

CPU_THRESHOLD = 80
BW_THRESHOLD = 60

# Detect host type (set in Mininet CLI before running)
HOST_TYPE = os.getenv("HOST_TYPE", "normal")  # default normal

prev_bw = {'sent':0, 'recv':0}

def get_bandwidth(prev):
    counters = psutil.net_io_counters()
    sent = counters.bytes_sent - prev['sent']
    recv = counters.bytes_recv - prev['recv']
    prev['sent'] = counters.bytes_sent
    prev['recv'] = counters.bytes_recv
    return (sent + recv) / 1024  # KB per interval

# Simulate CPU load
cpu = psutil.cpu_percent(interval=1)
bw  = get_bandwidth(prev_bw)

if HOST_TYPE == "cpu_stress":
    cpu += 50  # push CPU above threshold
elif HOST_TYPE == "bw_stress":
    bw += 100  # push bandwidth above threshold

print(f"{cpu},{bw}")

