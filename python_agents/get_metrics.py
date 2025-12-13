import psutil

CPU_THRESHOLD = 80
BW_THRESHOLD = 60

def get_bandwidth(prev):
    counters = psutil.net_io_counters()
    sent = counters.bytes_sent - prev['sent']
    recv = counters.bytes_recv - prev['recv']
    prev['sent'] = counters.bytes_sent
    prev['recv'] = counters.bytes_recv
    return (sent + recv) / 1024  # KB per interval

prev_bw = {'sent':0, 'recv':0}
cpu = psutil.cpu_percent(interval=1)
bw  = get_bandwidth(prev_bw)

print(f"{cpu},{bw}")

