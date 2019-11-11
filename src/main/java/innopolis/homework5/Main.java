package innopolis.homework5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Main implements Runnable {
    private ServerSocket serverSocket;
    private static final List<String> htmlPage = new ArrayList<>();
    final private static String PATH = ".";
    private String currPath = PATH;

    public Main(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }


    private List<String> parseRequest(String requestInfo) {
        String requestFolder = "";
        System.out.println(">>> " + requestInfo);

        List<String> requestArray = Arrays.asList(requestInfo.split(" "));
        String requestLine = requestArray.get(1);

        if (requestLine.contains("/?textinput=")) {
            String[] requestMass = requestArray.get(1).split("/\\?textinput=");
            if (!(requestMass.length == 0)) {
                requestFolder = requestMass[1];
            }
        } else {
            requestFolder = requestLine.substring(1, requestArray.get(1).length());
        }
        requestArray.set(1, requestFolder);
        return requestArray;
    }




    private ArrayList<String> prepareHtmlPage(List<String> requestList) {
        ArrayList<File> directoryNamesList = getDirectory();
        ArrayList<String> pageHTML = new ArrayList<>();

        switch (requestList.get(0)) {
            case "GET": {
                if ((checkFolder(directoryNamesList, requestList.get(1))) || (requestList.get(1).equals("favicon.ico"))) {
                    pageHTML.add("<p>Директория: " + currPath + "</p>");
                    directoryNamesList = getDirectory();
                    for (File currentElement : directoryNamesList) {
                        pageHTML.add(currentElement.getName());
                        pageHTML.add("<br />");
                    }
                } else {
                    if (requestList.get(1).equals("")) {
                        currPath = PATH;
                        pageHTML.add("<p>Директория: " + currPath + "</p>");
                        directoryNamesList = getDirectory();
                        for (File currentElement : directoryNamesList) {
                            pageHTML.add(currentElement.getName());
                            pageHTML.add("<br />");
                        }
                    } else {
                        currPath = PATH;
                        pageHTML.add("<p>Некорректная дирректория. Текущая директория: " + currPath + "</p>");
                        directoryNamesList = getDirectory();
                        for (File currentElement : directoryNamesList) {
                            pageHTML.add(currentElement.getName());
                            pageHTML.add("<br />");
                        }
                    }
                    break;
                }
                break;
            }
            case "POST": {
                pageHTML.add("HTTP/1.1 404 ERROR");
                break;
            }
            case "DELETE": {
                pageHTML.add("HTTP/1.1 404 ERROR");
                break;
            }
            case "PUT": {
                pageHTML.add("HTTP/1.1 404 ERROR");
                break;
            }
            case "PUTCH": {
                pageHTML.add("HTTP/1.1 404 ERROR");
                break;
            }
            default: {
                pageHTML.add("HTTP/1.1 404 ERROR");
                break;
            }
        }
        return pageHTML;
    }


    public void run() {
        while (true) {
            try (Socket clientInputRequest = serverSocket.accept();
                 BufferedReader input = new BufferedReader(new InputStreamReader(clientInputRequest.getInputStream()));
                 PrintWriter output = new PrintWriter(clientInputRequest.getOutputStream(), true)) {

                System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");

                if (input.ready()) {
                    System.out.println();
                    String requestInfo = input.readLine();
                    List<String> requestList = parseRequest(requestInfo);
                    ArrayList<String> directoryPage = prepareHtmlPage(requestList);

                    if (!directoryPage.get(0).equals("HTTP/1.1 404 ERROR")) {
                        for (String str : htmlPage) {
                            output.println(str);
                        }
                        for (String str : directoryPage) {
                            output.println(str);
                        }
                    } else {
                        for (String str : directoryPage) {
                            output.println(str);
                        }
                    }
                    clientInputRequest.close();
                    System.out.println("Client disconnected!");
                }
                System.out.println();
            } catch (SocketTimeoutException s) {
                System.out.println("Socket timed out!");
                break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }


    private boolean checkFolder(ArrayList<File> filesArray, String requestStr) {
        for (File file : filesArray) {
            if (file.getName().equals(requestStr)) {
                if (file.isDirectory()) {
                    currPath += "\\" + requestStr;
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }


    private static void startServer(int port) {
        readStandartHtmlPage(".\\resources\\page.html");
        try {
            Runnable t = new Main(port);
            t.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private ArrayList<File> getDirectory() {
        File[] files = new File(currPath).listFiles();
        ArrayList<File> results = new ArrayList<>();
        for (File file : files) {
            if (file != null) {
                results.add(file);
                System.out.println(file.getName());
            }
        }
        return results;
    }


    private static void readStandartHtmlPage(String htmlPagePath) {
        try(BufferedReader fileReader = new BufferedReader(new FileReader(htmlPagePath))) {
            String tempStr;
            while ((tempStr = fileReader.readLine()) != null) {
                htmlPage.add(tempStr);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    public static void main(String[] args) {
        startServer(8880);
    }
}


