import socket, threading,json
import time

import numpy as np

HOST = "127.0.0.1"
PORT = 5555

count = 0
clock = 0



def listen():
    global map
    global sent
    global count
    global clock
    while True:
        data = b""
        while not data:
            data = s.recv(1024)
            count = count + 1
            if data.decode().__contains__("P1") and not ("ack") in data.decode():
                map = json.loads(data.decode())
        if count > 1:
            print("\b\bP2: ", str(map[data.decode().split(":")[0]])+":"+data.decode().split(":")[1], "\n ", end = "")
            if not ("ack") in data.decode():
                print("lamports clock is updated too: ", max(clock, int(data.decode().split(":")[2])) + 1)
                clock = int(data.decode().split(":")[2])


def get_input():
    global clock
    global sent
    while True:
        data = b""
        while not data:
            data = input("> ").encode()
            print("P2: ", data.decode())
        sent = data

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect((HOST, PORT))
    sent = b""
    listen_thread = threading.Thread(target = listen)
    input_thread = threading.Thread(target = get_input)
    listen_thread.start()
    input_thread.start()
    while True:
        if sent:
            clock = clock + 1
            sent = str(sent) +"," + str(clock)
            s.sendall(sent.encode())
            sent = b""