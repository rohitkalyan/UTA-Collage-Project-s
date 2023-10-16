import java.io.*;
import java.net.Socket ;


public class Client {
        private static final String SERVER_IP = "127.0.0.1";
        private static final int SERVER_PORT = 9090;

        // Calling function to perform file upload Operation.
        private void callUploadFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the full path of the directory with file name Ex: /home/UserName/Ds/Project/dir2/file1.txt");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
                System.out.println("Enter the full path of the directory with file name Ex: /home/UserName/Ds/Project/dir1/file1.txt");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Calling function to perform file Download Operation.
        private void callDownloadFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the file to be Downlaoded: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Calling function to perform file Rename Operation.
        private void callRenameFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the filename: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
                System.out.println("Enter the newfilename: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Calling function to perform file Delete Operation.
        private void callDeleteFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the file to be Deleted: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Synchronous function call to perform ADD Operation.
        public void callAdd(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter first number:");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
                System.out.println("Enter second number: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // ASynchronous function call to perform ADD Operation.
        private void callAddAsync(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter first number :");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
                System.out.println("Enter second number : ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Synchronous function call to perform Matrix Operation.
        private void matrix(String matrix1, String matrix2, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the 2D  3X3 Array 1 Ex: 1,1,1:2,2,2:3,3,3");
                matrix1 = keyboardRead.readLine();
                pwrite.println(matrix1);
                System.out.println("Enter the 2D 3X3 Array 1 Ex: 1,1,1:2,2,2:3,3,3");
                matrix2 = keyboardRead.readLine();
                pwrite.println(matrix2);
        }

        // ASynchronous function call to perform Matrix Operation.
        private void matrixasync(String matrix1, String matrix2, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the 2D  3X3 Array 1 Ex: 1,1,1:2,2,2:3,3,3");
                matrix1 = keyboardRead.readLine();
                pwrite.println(matrix1);
                System.out.println("Enter the 2D 3X3 Array 1 Ex: 1,1,1:2,2,2:3,3,3");
                matrix2 = keyboardRead.readLine();
                pwrite.println(matrix2);
        }

        // Synchronous function call to perform sort Operation.
        private void callsortFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the numbers as camma seperated Ex: 8,5,7,3: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // ASynchronous function call to perform sort Operation.
        private void callsortAsyncFun(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the numbers as Async camma seperated Ex: 8,5,7,3: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // Synchronous function call to perform pi Operation.
        public void pi(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the radius of the Circle: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }

        // ASynchronous function call to perform pi Operation.
        public void piasync(String requestMessage, BufferedReader keyboardRead, PrintWriter pwrite) throws IOException {
                System.out.println("Enter the radius of the Circle: ");
                requestMessage = keyboardRead.readLine();
                pwrite.println(requestMessage);
        }


        public static void main(String[] args) throws IOException {
                Client client = new Client();
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                BufferedReader keyboardRead = new BufferedReader(new InputStreamReader(System.in));

                BufferedReader receiveRead = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pwrite = new PrintWriter(socket.getOutputStream(), true);

                String responseMessage, requestMessage, temp;
                while (true) {
                        System.out.println("\nEnter operation to perform(\n 1. upload\n 2. download\n 3. rename\n 4. delete\n " +
                                                "5. add\n 6. addasync\n 7. sort\n 8. sortasync\n 9. matrix\n 10. matrixasync\n 11. pi\n 12. piasync\n");
                        temp = keyboardRead.readLine();
                        requestMessage = temp.toLowerCase();
                        pwrite.println(requestMessage);
                        if (requestMessage.equalsIgnoreCase("upload")) {
                                client.callUploadFun(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("download")) {
                                client.callDownloadFun(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("rename")) {
                                client.callRenameFun(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("delete")) {
                                client.callDeleteFun(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("add")) {
                                client.callAdd(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("addasync")) {
                                client.callAddAsync(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("sort")) {
                                client.callsortFun(requestMessage, keyboardRead, pwrite);
                        }
                        if (requestMessage.equalsIgnoreCase("sortasync")) {
                                client.callsortAsyncFun(requestMessage, keyboardRead, pwrite);
                        }
                        if(requestMessage.equalsIgnoreCase("matrix")){
                                client.matrix(requestMessage, requestMessage, keyboardRead, pwrite);
                        }
                        if(requestMessage.equalsIgnoreCase("matrixasync")){
                                client.matrixasync(requestMessage, requestMessage, keyboardRead, pwrite);
                        }
                        if(requestMessage.equalsIgnoreCase("pi")){
                                client.pi(requestMessage, keyboardRead, pwrite);
                        }
                        if(requestMessage.equalsIgnoreCase("piasync")){
                                client.piasync(requestMessage, keyboardRead, pwrite);
                        }
                        if(requestMessage.equalsIgnoreCase("exit")){
                                System.out.println("Please enter the valid operation");
                        }
                        System.out.flush();
                        if ((responseMessage = receiveRead.readLine()) != null) {
                                System.out.println(responseMessage);
                        }
                }
        }
}