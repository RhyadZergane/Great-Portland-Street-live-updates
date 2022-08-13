package com.transform.underground;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.transform.underground.model.Line;
import com.transform.underground.model.Platform;
import com.transform.underground.model.StationArrivals;

import java.net.*;
import java.io.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    static final int SECONDS_IN_MILLIS = 1000;

    static final String GREAT_PORTLAND_STREET_ENDPOINT =
            "https://api.tfl.gov.uk/StopPoint/940GZZLUGPS/Arrivals?mode=tube";

    static final DateTimeFormatter ISO_INSTANT_FORMAT = DateTimeFormatter.ISO_INSTANT;

    public static class SortByArrivalTime implements Comparator<StationArrivals>{
        @Override
        public int compare(StationArrivals o1, StationArrivals o2) {
            return o1.getExpectedArrival().compareTo(o2.getExpectedArrival());
        }
    }

    public static class SortByPlatformDirection implements Comparator<StationArrivals>{
        @Override
        public int compare(StationArrivals o1, StationArrivals o2) {
            return o1.getPlatform().getName().compareTo(o2.getPlatform().getName());
        }
    }

    public static class SortByPlatformNumber implements Comparator<StationArrivals>{
        @Override
        public int compare(StationArrivals o1, StationArrivals o2) {
            String platformO1 = o1.getPlatform().getName();
            String platformO2 = o2.getPlatform().getName();

            // Only look at platform number and compare the platrform numbers
            return platformO1.substring(platformO1.length() - 1)
                    .compareTo(platformO2.substring(platformO2.length() -1));
        }
    }

    public static void outputArrivalsToFile(List<StationArrivals> arrivals){
        LocalDate today = LocalDate.now();
        try {
            File arrivalsFile = new File(System.getProperty("user.dir") +
                    "\\Great portland street arrivals " + today.getDayOfMonth()+ " " +
                    today.getMonth().name().charAt(0) + today.getMonth().name().substring(1).toLowerCase()
                    + " " + today.getYear() + ".txt");

            if (!arrivalsFile.exists()) {
                arrivalsFile.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(arrivalsFile, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            arrivals.forEach(arrival -> {
                try {
                    bufferedWriter.write(arrival.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Append new line after every update
            bufferedWriter.write('\n');

            bufferedWriter.close();
        }
        catch (Exception e){
            System.out.println(e);
        }
    }

    public static void getGreatPortLandStreetArrivals(){
        List<StationArrivals> greatPortLandStreet = new LinkedList<>();
        try {
            // way to accomplish http request from java 11
            HttpClient webclient = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(GREAT_PORTLAND_STREET_ENDPOINT))
            .GET().build();

            String greatPortlandStreetArrivalsRawJsonString = webclient.
                    sendAsync(request, HttpResponse.BodyHandlers.ofString())
                   .get().body();

            // legacy way
            /*URL url = new URL(GREAT_PORTLAND_STREET_ENDPOINT);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuilder result = new StringBuilder();

            conn.setRequestMethod("GET");
            // 10 second timeout
            conn.setConnectTimeout(10 * SECONDS_IN_MILLIS);
            conn.setReadTimeout(10 * SECONDS_IN_MILLIS);


            BufferedReader reader = new BufferedReader(new InputStreamReader(apiResponse));

            String line;

            while((line = reader.readLine()) != null){
                result.append(line);
            }
            reader.close();

             */
            JsonArray greatPortlandStreetArrivals
                    = JsonParser.parseString(greatPortlandStreetArrivalsRawJsonString).getAsJsonArray();

            for(JsonElement arrival: greatPortlandStreetArrivals){
                /*String dest = arrival.getAsJsonObject().get("towards") == null? "":
                        arrival.getAsJsonObject().get("towards").getAsString();*/

                String dest = arrival.getAsJsonObject().get("towards").getAsString();

                String currentLocation = arrival.getAsJsonObject().get("currentLocation").getAsString();

                // Parsed this way as tfl sends expected
                String dateString = arrival.getAsJsonObject().get("expectedArrival").getAsString();
                LocalTime expected = LocalTime.ofInstant(
                        Instant.from(ISO_INSTANT_FORMAT.parse(dateString)), ZoneId.of("UTC"));

                String direction = arrival.getAsJsonObject().get("direction") == null? "":
                        arrival.getAsJsonObject().get("direction").getAsString();
                Platform platform = Platform.builder()
                        .name(arrival.getAsJsonObject().get("platformName").getAsString()).build();
                Line trainLine = Line.builder().name(arrival.getAsJsonObject().get("lineName").getAsString()).build();
                StationArrivals stationArrivals = StationArrivals.builder().line(trainLine)
                        .stationName(arrival.getAsJsonObject().get("stationName").getAsString())
                        .direction(direction)
                        .platform(platform)
                        .expectedArrival(expected)
                        .currentLocation(currentLocation)
                        .destination(dest)
                        .build();
                greatPortLandStreet.add(stationArrivals);
            }

            greatPortLandStreet.sort(new SortByPlatformNumber());
            greatPortLandStreet.forEach(System.out::println);

            // give an extra line to indicate when new batch of line data was recieved
            System.out.println();

            outputArrivalsToFile(greatPortLandStreet);
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    public static void fetchApiCallEveryNSeconds(long numSecs){
        // timer for polling api at n seconds
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getGreatPortLandStreetArrivals();
            }
        },0,numSecs * SECONDS_IN_MILLIS);
    }

    public static void main(String[] args) {

        fetchApiCallEveryNSeconds(5);
    }
}
