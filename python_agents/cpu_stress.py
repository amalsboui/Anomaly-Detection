# python_agents/cpu_stress.py
import time

# Simple CPU-heavy loop
while True:
    x = 0
    for i in range(10_000_000):  # enough to stress CPU
        x += i*i
    time.sleep(0.1)

