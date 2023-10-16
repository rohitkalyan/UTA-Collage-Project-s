import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class ClientHandler implements  Runnable{

//    String directory1 = "C:\\MyWork\\savedFiles\\Directory1\\";
//    String directory2 = "C:\\MyWork\\savedFiles\\Directory2\\";
    String sortArr;
    String pathToFile;
    String sortAsyncArr;
    int async_Sum;
    double asyncPI;
    int[] numStringArray;
    int[] numStringAyncArray;
    private Socket client;
    private BufferedReader receiveRead;
    private PrintWriter pwrite;

    public  ClientHandler(Socket clientSocket) throws IOException {
        this.client = clientSocket;
        receiveRead = new BufferedReader(new InputStreamReader(client.getInputStream()));
        pwrite = new PrintWriter(client.getOutputStream(), true);
    }


    // Function for the File Upload.
    private void uploadFileCopy(String filename, String uploadDirectory,PrintWriter pwrite) throws IOException {
        File src = new File(filename);
        File dest = new File(uploadDirectory);
        Files.copy(src.toPath(), dest.toPath());

        System.out.println("Successfully uploaded the file. " + filename);
        pwrite.println("Successfully uploaded the file: " + filename);
    }

    // Function for the File Downaload.
    private void downloadFileRead(String filename, PrintWriter pwrite)throws IOException {
        pathToFile = filename;
        File serverFile = new File(pathToFile);
        byte[] fileData = new byte[(int) serverFile.length()];
        FileInputStream fileInputStream = new FileInputStream(pathToFile);
        fileInputStream.read(fileData, 0, fileData.length);
        fileInputStream.close();
        String downloadedResult = new String(fileData);
        pwrite.println("The data inside the file is: " + downloadedResult);

    }

    // Function for the File Renaming
    private void renamingOfFile(String filename, String newFileName, PrintWriter pwrite){
        File path = new File(filename);
        File newFile = new File(newFileName);
        path.renameTo(newFile);
        System.out.println("The file: "+ filename + "is changed too: "+ newFileName);
        pwrite.println("The file: "+ filename + " is changed too: "+ newFileName);
    }

    // Function for the file Delete.
    private void deletingOfFile(String filename, PrintWriter pwrite){
        File path = new File(filename);
        path.delete();
        pwrite.println("The File: " +filename+" is Successfully Deleted ....");
    }


    // Function for synchronous Add of two elements
    private  void synchronousAdd(int num1, int num2, PrintWriter pwrite) {
        int sync_sum = num1 + num2;
        System.out.println("sum of given numbers through synchronous RPC = " + sync_sum);
        pwrite.println("sum of given numbers through synchronous RPC = " + sync_sum);
    }

    // Function for Asynchronous Add of two elements
    private void asynchronousAdd(int num1, int num2, PrintWriter pwrite){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            async_Sum =  num1 + num2;
            System.out.println("sum of given numbers through Asynchronous RPC" + async_Sum);
            pwrite.println("sum of given numbers through Asynchronous RPC" + async_Sum);
            return "sum of given numbers through Asynchronous RPC" + String.valueOf(async_Sum);
        });
        completableFuture.thenAccept(System.out::println);
        pwrite.println("sum of given numbers through Asynchronous RPC" + async_Sum);
    }

    // Function for synchronous call for calulating the PI value.
    private  void synchronousPI(int radiusNum, PrintWriter pwrite) {
        double circumference = 2 * Math.PI * radiusNum;
        double sync_pi =  circumference/(2 * radiusNum);
        System.out.println("The value of PI through synchronous RPC: "+ sync_pi);
        pwrite.println("The value of PI through synchronous RPC: " + sync_pi);
    }

    // Function for asynchronous call for calulating the PI value.
    private void asynchronousPI(int radiusNum, PrintWriter pwrite){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            double circumference = 2 * Math.PI * radiusNum;
            double async_pi =  circumference/(2 * radiusNum);
            asyncPI = async_pi;
            pwrite.println("The value of PI through Asynchronous RPC: " + async_pi);
            return "The value of PI through Asynchronous RPC: "+ asyncPI;
        });
        completableFuture.thenAccept(System.out::println);
        pwrite.println("The value of PI through Asynchronous RPC: " + asyncPI);
    }

    // Function for synchronous call for sorting the Array.
    public void synchronousSort(String stringArr, PrintWriter pwrite){
        int[] numStringArray = Arrays.stream(stringArr.split(",")).mapToInt(Integer::parseInt).toArray();
        Arrays.sort(numStringArray);
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < numStringArray.length; i++) {
            sb.append(numStringArray[i]);
        }
        sortArr= Arrays.toString(numStringArray);
        System.out.println("The Sorted Array through synchronous RPC: " + sortArr);
        pwrite.println("The Sorted Array through synchronous RPC: " + sortArr);
    }

    // Function for asynchronous call for sorting the Array.
    public void asynchronousSort(String stringArr, PrintWriter pwrite){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            numStringAyncArray = Arrays.stream(stringArr.split(",")).mapToInt(Integer::parseInt).toArray();
            Arrays.sort(numStringAyncArray);
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < numStringAyncArray.length; i++) {
                sb.append(numStringAyncArray[i]);
            }
            sortAsyncArr= Arrays.toString(numStringAyncArray);
            System.out.println(sortAsyncArr);
            pwrite.println("The Sorted Array through Asynchronous RPC: " + sortAsyncArr);
            return "The Sorted Array through Asynchronous RPC: " + sortArr;
        });
        completableFuture.thenAccept(System.out::println);
    }

    // Function to convert a String into Matrix and the convert Matrix in integer Matrix
    public static int[][] strIntoInt(String matrix){
        String[] lines = matrix.split(":");
        int width = lines.length;
        String[] cells = lines[0].split(",");
        int height = cells.length;
        int[][] output = new int[width][height];

        for (int i=0; i<width; i++) {
            String[] cells1 = lines[i].split(",");
            for(int j=0; j<height; j++) {
                output[i][j] = Integer.parseInt(cells1[j]);
            }
        }
        return output;
    }


    // Function for synchronous call for the matrix multiplication.
    public void synchronousMatrixMultiplication(String matrix1, String matrix2, PrintWriter pwrite){

        int[][] matrixInt1 = strIntoInt(matrix1);
        int[][] matrixInt2 = strIntoInt(matrix2);
        int[][] output = new int[3][3];

        for(int i=0;i<3;i++) {
            for (int j = 0; j < 3; j++) {
                output[i][j] = 0;
                for (int k = 0; k < 3; k++) {
                    output[i][j] += matrixInt1[i][k] * matrixInt2[k][j];
                }
                System.out.print(output[i][j]+" ");
            }
            System.out.println();
        }
        String result =  Arrays.deepToString(output);
        pwrite.println("The Resultant Matrix through synchronous RPC: " + result);
    }


    // Function for asynchronous call for the matrix multiplication.
    public void asynchronousMatrixMultiplication(String matrix1, String matrix2, PrintWriter pwrite){
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            int[][] matrixInt1 = strIntoInt(matrix1);
            int[][] matrixInt2 = strIntoInt(matrix2);
            int[][] output = new int[3][3];

            for(int i=0;i<3;i++) {
                for (int j = 0; j < 3; j++) {
                    output[i][j] = 0;
                    for (int k = 0; k < 3; k++) {
                        output[i][j] += matrixInt1[i][k] * matrixInt2[k][j];
                    }
                    System.out.print(output[i][j]+" ");
                }
                System.out.println();
            }
            String result =  Arrays.deepToString(output);
            pwrite.println("The Resultant Matrix through Asynchronous RPC: " + result);
            return "The Resultant Matrix through Asynchronous RPC: " + result;
        });
        completableFuture.thenAccept(System.out::println);
    }

    // The Run method will override the Thread Run function.
    @Override
    public void run(){
        try {
            ClientHandler clientHandler = new ClientHandler(client);
            String operation, filename, newfilename, matrix1, matrix2;
            int num1, num2;
            while (true) {
                operation = receiveRead.readLine();
                if (operation != null) {
                    System.out.println("The operation requested by the Client is: " + operation);
                }
                if (operation.compareTo("upload") == 0) {
                    filename =  receiveRead.readLine();
                    System.out.println("Given the path of file to be uploaded "+ filename);
                    newfilename = receiveRead.readLine();
                    System.out.println("Uploaded the file in the given path: " + newfilename);
                    clientHandler.uploadFileCopy(filename, newfilename, pwrite);
                }
                if (operation.compareTo("download") == 0) {
                    filename =  receiveRead.readLine();
                    clientHandler.downloadFileRead(filename, pwrite);
                }
                if (operation.compareTo("rename") == 0) {
                    filename =  receiveRead.readLine();
                    System.out.println("The filename need to be changed is: "+ filename);
                    newfilename = receiveRead.readLine();
                    clientHandler.renamingOfFile(filename, newfilename, pwrite);
                }
                if (operation.compareTo("delete") == 0) {
                    filename =  receiveRead.readLine();
                    clientHandler.deletingOfFile(filename, pwrite);
                }
                if (operation.compareTo("add") == 0) {
                    num1 = Integer.parseInt(receiveRead.readLine());
                    System.out.println("Parameter 1 : " + num1);
                    num2 = Integer.parseInt(receiveRead.readLine());
                    clientHandler.synchronousAdd(num1,num2, pwrite);
                }
                if (operation.compareTo("addasync") == 0) {
                    num1 = Integer.parseInt(receiveRead.readLine());
                    System.out.println("Parameter 1 : " + num1);
                    num2 = Integer.parseInt(receiveRead.readLine());
                    clientHandler.asynchronousAdd(num1,num2, pwrite);
                }
                if (operation.compareTo("sort") == 0) {
                    filename =  receiveRead.readLine();
                    clientHandler.synchronousSort(filename, pwrite);
                }
                if (operation.compareTo("sortasync") == 0) {
                    filename =  receiveRead.readLine();
                    clientHandler.asynchronousSort(filename, pwrite);
                }
                if (operation.compareTo("matrix") == 0) {
                    matrix1 = receiveRead.readLine();
                    matrix2 = receiveRead.readLine();
                    clientHandler.synchronousMatrixMultiplication(matrix1,matrix2, pwrite);
                }
                if (operation.compareTo("matrixasync") == 0) {
                    matrix1 = receiveRead.readLine();
                    matrix2 = receiveRead.readLine();
                    clientHandler.asynchronousMatrixMultiplication(matrix1,matrix2, pwrite);
                }
                if (operation.compareTo("pi") == 0) {
                    num1 = Integer.parseInt(receiveRead.readLine());
                    System.out.println("Parameter 1 : " + num1);
                    clientHandler.synchronousPI(num1,pwrite);
                }
                if (operation.compareTo("piasync") == 0) {
                    num1 = Integer.parseInt(receiveRead.readLine());
                    System.out.println("Parameter 1 : " + num1);
                    clientHandler.asynchronousPI(num1,pwrite);
                }
                System.out.flush();
            }
        }catch (IOException e){
            System.err.println("IOException in handler");
            System.err.println(e.getStackTrace());
        }
        finally {
            pwrite.close();
            try {
                receiveRead.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
