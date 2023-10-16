import socket
import numpy as np
from threading import Thread
import time
import logging
import traceback

ip= "127.0.0.1"
port1= 9093
port2= 9096
sock= socket.socket(socket.AF_INET, socket.SOCK_STREAM)

i=2
clock= [0, 0, 0]

def sender():
    global clock
    while True:
        node3 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node3.bind((ip, port2))
        while True:
            print("To Send ok msg to client_1 Press 1 \n"+ "To Send ok msg to client_2 Press 2 \n")
            no = input("Send message to Client 1 or 2: ")
            if no not in ('1', '2'):
                print("Please enter correct number.")
            else:
                fport = 9090 + int(no)
                clock[i] += 1
                msg = ''.join(str(x) + ' ' for x in clock) + ' '
                inp = input("Enter the message to send:")
                print(clock, end='')
                msg += inp
                try:
                    try:
                        node3.sendto(msg.encode(), (ip, fport))
                    except:
                        node3 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                        node3.connect((ip, fport))
                        node3.sendto(msg.encode(), (ip, fport))
                        # sock2.close()
                except Exception as e:
                    logging.error(traceback.format_exc())
                    print("Msg cannot be sent!!")
                    clock[i] -= 1
                print(clock)
                break
        node3.shutdown(socket.SHUT_RDWR)
        time.sleep(3)

def listener(sock, addr):
    global clock
    #print('Got a msg from', addr)
    try:
        msg = sock.recv(1024)
        print(clock,end='')
        clock[i]+=1
        #print(msg.decode().split('  ')[1])
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
    print("Please Enter the msg")
    sock.bind((ip, port1))
    sock.listen(3)
    #sock2.bind((ip,port2))
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
