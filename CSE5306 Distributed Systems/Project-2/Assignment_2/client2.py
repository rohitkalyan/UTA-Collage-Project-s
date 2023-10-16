import socket
import numpy as np
from threading import Thread
import time
import logging
import traceback

ip= "127.0.0.1"
port1= 9092
port2= 9095
sock= socket.socket(socket.AF_INET, socket.SOCK_STREAM)

i=1
clock = [0, 0, 0]

def sender():
    global clock
    while True:
        node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node2.bind((ip, port2))
        while True:
            print("To Send ok msg to client_1 Press 1 \n"+ "To Send ok msg to client_3 Press 3 \n")
            machine_no = input("Send message to Client 1 or 3: ")
            if machine_no not in ('1', '3'):
                print("Please enter correct number.")
            else:
                fport = 9090 + int(machine_no)
                clock[i] += 1
                msg = ''.join(str(x) + ' ' for x in clock) + ' '
                inp = input("Enter the message to send:")
                print(clock, end='')
                msg += inp
                try:
                    try:
                        node2.sendto(msg.encode(), (ip, fport))
                    except:
                        node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        node2.connect((ip, fport))
                        node2.sendto(msg.encode(), (ip, fport))
                except Exception as e:
                    logging.error(traceback.format_exc())
                    print("Couldn't send msg!")
                    clock[i] -= 1
                print(clock)
                break
        node2.shutdown(socket.SHUT_RDWR)
        time.sleep(3)

def listener(sock, addr):
    global clock
    try:
        msg = sock.recv(1024)
        print("Intial Clock: ")
        print(clock,end='')
        print("\n")
        clock[i]+=1
        l= [int(x) for x in list(msg.decode().split('  ')[0]) if x.isdigit()]
        clock=np.maximum(clock,l)
        print("Final Clock: ")
        print(clock,end='')
        print("\n")
        print("Message: ")
        print(msg.decode().split('  ')[1])
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
                t2= Thread(target=listener, args=(conn, addr,), daemon=True)
                t2.start()
            time.sleep(1)
        except Exception as e:
            logging.error(traceback.format_exc())
