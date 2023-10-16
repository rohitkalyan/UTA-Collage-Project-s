import socket, time, logging, traceback
import numpy as np
from threading import Thread

ip= "127.0.0.1"
port1= 9091
port2= 9094
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

inc=0
clock= [0, 0, 0]

def sender():
    global clock
    while True:
        node2 = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        node2.bind((ip, port2))
        while True:
            print("To Send ok msg to client_2 Press 2 \n"+ "To Send ok msg to client_3 Press 3 \n")
            no = input("Send message to Client 2 or 3: ")
            fport = 9090 + int(no)
            clock[inc] += 1
            comms = ''.join(str(x) + ' ' for x in clock) + ' '
            text = input("Enter the message: ")
            print(clock, end='')
            comms += text
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
                clock[inc] -= 1
            print(clock)
            break
        node2.shutdown(socket.SHUT_RDWR)
        time.sleep(3)

def listener(sock, addr):
    global clock
    try:
        msg = sock.recv(1024)
        print(clock, end='')
        clock[inc]+=1
        l= [int(x) for x in list(msg.decode().split('  ')[0]) if x.isdigit()]
        clock=np.maximum(clock, l)
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
    thread1=None
    thread2=None
    thread1= Thread(target=sender, args=(), daemon=True)
    thread1.start()
    while True:
        try:
            conn, addr= sock.accept()
            if conn:
                thread2= Thread(target=listener, args=(conn, addr,), daemon=True)
                thread2.start()
            time.sleep(1)
        except Exception as e:
            logging.error(traceback.format_exc())
