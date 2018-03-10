package com.example.ripan.map;

import android.os.*;
import android.util.*;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.*;
import java.util.function.*;

public class Communication extends AsyncTask<Communication.Request, Void, ArrayList<Communication.Request>> {
    private static final String host = "oisincar.netsoc.ie";
    private static final int port = 80;

    public static abstract class Request {
        private boolean successful;

        public Request() {
            successful = false;
        }

        public abstract String getRequestString();
        public abstract void processResponse(String response);
        public abstract void inform();

        public void requestFailed() {
            successful = false;
        }

         void requestSucceeded() {
            successful = true;
        }

        public boolean wasSuccessful() {
            return  successful;
        }
    }

    public static class MessagesGetRequest extends Request {
        private String result;
        private Consumer<MessagesGetRequest> resultHandler;

        public MessagesGetRequest(Consumer<MessagesGetRequest> handler) {
            if (handler == null) {
                throw new IllegalArgumentException();
            }

            resultHandler = handler;
        }

        public String getRequestString() {
            String request = "";
            request += "GET /chat/ HTTP/1.1\r\n";
            request += "Host: " + host + "\r\n";
            request += "Connection: close\r\n";
            request += "\r\n";
            return request;
        }

        public void processResponse(String response) {
            String[] lines = response.split("\r\n");
            String statusLine = "";
            StringBuilder resultBuilder = new StringBuilder();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                if (i == 0) {
                    statusLine = line;
                }else if (line.equals("")) {
                    for (i++; i < lines.length; i++) {
                        resultBuilder.append(lines[i] + "\n");
                    }
                }
            }

            if (statusLine.equals("HTTP/1.1 200 OK") == false) {
                requestFailed();
                return;
            }

            result = resultBuilder.toString();
        }

        public void inform() {
            resultHandler.accept(this);
        }

        public String getResult() {
            return result;
        }
    }

    public static class MessagesPutRequest extends Request {
        private String identifier;
        private String data;
        private Consumer<MessagesPutRequest> resultHandler;

        public MessagesPutRequest(String id, String in, Consumer<MessagesPutRequest> handler) {
            if (in == null || handler == null) {
                throw new IllegalArgumentException();
            }

            identifier = id;
            data = in;
            resultHandler = handler;
            Log.v("MessagesPutRequest", "Created put request");
        }

        public String getRequestString() {
            String request = "";
            request += "PUT /chat/ HTTP/1.1\r\n";
            request += "Host: " + host + "\r\n";
            request += "Connection: close\r\n";
            request += "Content-type: application/json\r\n";
            request += "Content-length: " + data.length() + "\r\n";
            request += "\r\n";
            request += data + "\r\n";
            request += "\r\n";
            return request;
        }

        public void processResponse(String response) {
            String[] lines = response.split("\r\n");
            String statusLine = "";
            StringBuilder resultBuilder = new StringBuilder();

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                if (i == 0) {
                    statusLine = line;
                }else if (line.equals("")) {
                    for (i++; i < lines.length; i++) {
                        resultBuilder.append(lines[i]);
                    }
                }
            }

            if (statusLine.equals("HTTP/1.1 200 OK") == false) {
                requestFailed();
                return;
            }

            String result = resultBuilder.toString();

            if (result.equals("{\n  \"resp\": \"Message posting sucessful\"\n}") == false) {
                Log.e("Communication", "Server did not return correct string. :" + result);
                requestFailed();
                return;
            }
        }

        public void inform() {
            Log.v("MessagesPutRequest", "Inform");
            resultHandler.accept(this);
        }

        public String getIdentifier() {
            return identifier;
        }
    }

    protected ArrayList<Request> doInBackground(Request... requests) {
        Log.v("Communication", "Started do in background");

        ArrayList<Request> processedRequests = new ArrayList<>();

        for (Request request : requests) {
            InetAddress serverAddress;
            Socket socket;

            try {
                serverAddress = InetAddress.getByName(host);
                socket = new Socket(serverAddress, port);

            }catch (UnknownHostException ex) {
                Log.e("Communication", "Did not find host: " + host, ex);
                request.requestFailed();
                continue;

            }catch (IOException ex) {
                Log.e("Communication","Can not create socket: " + host + ":" + port, ex);
                request.requestFailed();
                continue;
            }

            InputStream inputStream;
            OutputStream outputStream;

            try {
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();
            } catch (Exception ex) {
                Log.e("Communication","Can not open I/O streams for socket: " + host + ":" + port, ex);
                request.requestFailed();
                continue;
            }

            Scanner input = new Scanner(inputStream).useDelimiter("\0");
            PrintWriter output = new PrintWriter(outputStream);

            output.write(request.getRequestString());
            output.flush();

            StringBuilder responseBuilder = new StringBuilder();

            while (input.hasNext()) {
                responseBuilder.append(input.next());
            }

            input.close();
            output.close();

            request.requestSucceeded();
            request.processResponse(responseBuilder.toString());

            processedRequests.add(request);
            Log.v("Communication", "Assuming successful send.");

        }

        Log.v("Communication", "Ended do in background");

        return processedRequests;
    }

    @Override
    protected void onPostExecute(ArrayList<Request> processedRequests) {
        Log.v("Communication", "Started onPostExecute.");

        for (Request request : processedRequests) {
            request.inform();
        }
        Log.v("Communication", "Ended onPostExecute.");

    }

    @Override
    protected void onCancelled(ArrayList<Request> requests) {
        Log.v("Communication", "Requests canceled");
    }
}
