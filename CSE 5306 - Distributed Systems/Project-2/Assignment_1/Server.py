import socket, threading, json
import time

HOST = "127.0.0.1"
PORT = 5555

map = {}
clock = ""


def listen(socket):
    global clock
    while True:
        data = b""
        while not data:
            data = socket.recv(1024)
            print("\b\bReceived: ", data.decode(), "\n> ", end = "")
        clock = data.decode().split(",")[1]
        sent[socket] = data


with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.bind((HOST, PORT))
    s.listen()
    conn1, addr1 = s.accept()
    print("Connected to", addr1)
    conn2, addr2 = s.accept()
    print("Connected to", addr2)
    conn3, addr3 = s.accept()
    print("Connected to", addr3)
    sent = {conn1:b"", conn2:b"", conn3:b""}
    map[str(addr1).split(",")[1].replace(" ", "")[:-1]] = "P1"
    map[str(addr2).split(",")[1].replace(" ", "")[:-1]] = "P2"
    map[str(addr3).split(",")[1].replace(" ", "")[:-1]] = "P3"
    thread1 = threading.Thread(target = listen, args = (conn1, ))
    thread2 = threading.Thread(target = listen, args = (conn2, ))
    thread3 = threading.Thread(target = listen, args = (conn3, ))
    thread1.start()
    thread2.start()
    thread3.start()

    print(map)
    conn1.sendall(json.dumps(map).encode())
    conn2.sendall(json.dumps(map).encode())
    conn3.sendall(json.dumps(map).encode())
    while True:
        if sent[conn1]:
            msg = str(addr1).split(",")[1].replace(" ", "")[:-1] + ":" + sent[conn1].decode("utf-8") + ":"+ str(int(clock) + 1)
            print(msg)
            conn2.sendall(msg.encode())
            conn3.sendall(msg.encode())
            time.sleep(1)
            ack = str(addr1).split(",")[1].replace(" ", "")[:-1] + ":" + "P2 and P3 are ack"
            conn1.sendall(ack.encode())
            sent[conn1] = b""
        if sent[conn2]:
            msg = str(addr2).split(",")[1].replace(" ", "")[:-1] + ":" + sent[conn2].decode("utf-8") + ":" + str(int(clock) + 1)
            conn1.sendall(msg.encode())
            conn3.sendall(msg.encode())
            time.sleep(1)
            ack = str(addr2).split(",")[1].replace(" ", "")[:-1] + ":" + "P1 and P3 are ack"
            conn2.sendall(ack.encode())
            sent[conn2] = b""
        if sent[conn3]:
            msg = str(addr3).split(",")[1].replace(" ", "")[:-1] + ":" + sent[conn3].decode("utf-8") + ":" + str(int(clock) + 1)
            conn1.sendall(msg.encode())
            conn2.sendall(msg.encode())
            time.sleep(1)
            ack = str(addr3).split(",")[1].replace(" ", "")[:-1] + ":" + "P1 and P2 are ack"
            conn3.sendall(ack.encode())
            sent[conn3] = b""