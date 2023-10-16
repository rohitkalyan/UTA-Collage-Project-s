import socket, time, logging, traceback
import numpy as np
from threading import Thread

ip= "127.0.0.1"
port1= 9093
port2= 8083
port3= 7073
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

request = 0


def sender():
    global request
    while True:
        node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node2.bind((ip, port2))
        while True:
            print("To Send ok msg to client_1 Press 1 \n"+ "To Send ok msg to client_2 Press 2 \n")
            no = input("Send message to Client 1 or 2 : \n")
            fport = 9090 + int(no) + 3
            comms = "Server3 sends :ok"
            try:
                try:
                    node2.sendto(comms.encode(), (ip, fport))
                except:
                    node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    node2.connect((ip, fport))
                    node2.sendto(comms.encode(), (ip, fport))
            except Exception as e:
                logging.error(traceback.format_exc())
                print("Couldn't send comms!")
            break
        node2.shutdown(socket.SHUT_RDWR)
        time.sleep(3)

def recever(sock, addr):
    global request
    try:
        msg = sock.recv(1024)
        print(msg.decode())
        time.sleep(5)
        if msg.decode() == "yes":
            request = request + 1
            print("The no of request arraived is: ", request)
    except Exception as e:
        logging.error(traceback.format_exc())

if __name__ == "__main__":
    print("Waiting for msgs")
    sock.bind((ip, port1))
    sock.listen(3)
    print(sock)
    thread1=None
    thread2=None
    thread1= Thread(target=sender, args=(), daemon=True)
    thread1.start()
    while True:
        try:
            conn, addr= sock.accept()
            if conn:
                thread2= Thread(target=recever, args=(conn, addr,), daemon=True)
                thread2.start()
            time.sleep(1)
        except Exception as e:
            logging.error(traceback.format_exc())
