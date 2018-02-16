package com.example.ripan.map;

import android.os.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class DataFetcher extends AsyncTask<DataFetcher.DataRequest, Integer, String> {
    public static class DataRequest {
        public final String serverIP;
        public final int serverPort;

        public DataRequest(String serverIP, int serverPort) {
            this.serverIP = serverIP;
            this.serverPort = serverPort;
        }
    }

    protected String doInBackground(DataRequest... requests) {
        String results = "";

        for (DataRequest request : requests) {
            try {
                InetAddress serverAddress = InetAddress.getByName(request.serverIP);
                Socket socket = new Socket(serverAddress, request.serverPort);

                InputStream inputStream;
                OutputStream outputStream;

                try {
                    inputStream = socket.getInputStream();
                    outputStream = socket.getOutputStream();
                } catch (Exception e) {
                    continue;
                }

                Scanner input = new Scanner(inputStream).useDelimiter("\0");
                PrintWriter out = new PrintWriter(outputStream);

                out.write("GET /chat HTTP/1.1\r\n\r\n");
                out.flush();

                String result = "";

                while (input.hasNext()) {
                    result += input.next();
                }

                input.close();
                out.close();

                results += result + "\n";

            } catch (Exception e) {
                continue;
            }
        }

        return  results;
    }

}
