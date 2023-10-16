import json
import socket
from threading import Thread
import time
import logging
import traceback
import os

ip= "127.0.0.1"
port1= 9095
port2= 8085
port3= 7075
port4= 6075

count = 0
sock= socket.socket(socket.AF_INET, socket.SOCK_STREAM)


def sender():
    print("Ask the server for the request to access the file")
    while True:
        node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node2.bind((ip, port2))
        node3 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node3.bind((ip, port3))
        node4 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node4.bind((ip, port4))
        while True:
            fport1 = 9091
            fport2 = 9092
            fport3 = 9093
            comms = "Client2 asks: Requesting file access "
            try:
                try:
                    node2.sendto(comms.encode(), (ip, fport1))
                    node3.sendto(comms.encode(), (ip, fport2))
                    node4.sendto(comms.encode(), (ip, fport3))
                except:
                    node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    node2.connect((ip, fport1))
                    node2.sendto(comms.encode(), (ip, fport1))

                    node3 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    node3.connect((ip, fport2))
                    node3.sendto(comms.encode(), (ip, fport2))

                    node4 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    node4.connect((ip, fport3))
                    node4.sendto(comms.encode(), (ip, fport3))
            except Exception as e:
                logging.error(traceback.format_exc())
                print("Couldn't send comms!")
            break
        node2.shutdown(socket.SHUT_RDWR)
        time.sleep(20)

def fileLock():
    from filelock import FileLock

    file = str(os.path.expanduser('~\OneDrive\Desktop\\test1.txt'))
    print(file)
    lockfile = str(file) + ".lock"

    lock = FileLock(lockfile, timeout=10)

    lock.acquire()
    try:
        with open(file) as f:
            data = json.load(f)
            print("Data inside the file Before update:", data)
            data["UpdatedBy"] = "Client 2 "
            data["IncrementValue"] = int(data["IncrementValue"]) + 1
            json_object = json.dumps(data)
            f1 = open(file, 'w+')
            print("updated data inside test file :", json_object)
            f1.write(json_object)
            f.close()
            f1.close()
    finally:
        lock.release()

def recever(sock, addr):
    global count
    try:
        msg = sock.recv(1024)
        print("Message: ", msg.decode())
        if str(msg.decode()).split(":")[1] == "ok":
            count = count + 1
        if count == 3:
            count = 0
            fileLock()

    except Exception as e:
        logging.error(traceback.format_exc())

if __name__ == "__main__":
    print("Waiting for msgs")
    sock.bind((ip, port1))
    sock.listen(3)
    print(sock)
    t1=None
    t2=None
    t1= Thread(target=sender, args=(), daemon=True)
    t1.start()
    while True:
        try:
            conn, addr= sock.accept()
            if conn:
                t2= Thread(target=recever, args=(conn, addr,), daemon=True)
                t2.start()
            time.sleep(1)
        except Exception as e:
            logging.error(traceback.format_exc())
