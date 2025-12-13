import time
while True:
    x = 0
    for i in range(1000000):
        x += i*i
    time.sleep(0.1)

