package org.damose.controller;

import org.damose.model.*;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MyParser:
 * Classe di utilità per il parsing dei file GTFS CSV.
 * Supporta l'importazione di agency, routes, shapes, stops, trips e stop_times.
 */
public class MyParser {

    /**
     * Parsea il file agency.txt e restituisce la lista di Agency.
     */
    public static List<Agency> parseAgency(String filePath) throws IOException {
        List<Agency> agencies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String agencyId = row[0].trim();
                String agencyName = row.length > 1 ? row[1].trim() : "";
                String agencyUrl = row.length > 2 ? row[2].trim() : "";
                String agencyTimezone = row.length > 3 ? row[3].trim() : "";
                String agencyLang = row.length > 4 ? row[4].trim() : "";
                String agencyPhone = row.length > 5 ? row[5].trim() : "";
                String agencyFareUrl = row.length > 6 ? row[6].trim() : "";

                agencies.add(new Agency(agencyId, agencyName, agencyUrl, agencyTimezone,
                        agencyLang, agencyPhone, agencyFareUrl));
            }
        }
        return agencies;
    }

    /**
     * Parsea il file calendar_dates.txt e restituisce la lista di CalendarDate.
     */
    public static List<CalendarDate> parseCalendarDates(String filePath) throws IOException {
        List<CalendarDate> dates = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String serviceId = row[0].trim();
                String date = row[1].trim();
                int exceptionType = (row.length > 2 && !row[2].trim().isEmpty()) ? Integer.parseInt(row[2].trim()) : 0;

                dates.add(new CalendarDate(serviceId, date, exceptionType));
            }
        }
        return dates;
    }

    /**
     * Parsea il file routes.txt e restituisce la lista di Route.
     */
    public static List<Route> parseRoutes(String filePath) throws IOException {
        List<Route> routes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String routeId = row[0].trim();
                String agencyId = row.length > 1 ? row[1].trim() : "";
                String routeShortName = row.length > 2 ? row[2].trim() : "";
                String routeLongName = row.length > 3 ? row[3].trim() : "";
                int routeType = (row.length > 4 && !row[4].trim().isEmpty()) ? Integer.parseInt(row[4].trim()) : 0;
                String routeUrl = row.length > 5 ? row[5].trim() : "";
                String routeColor = row.length > 6 ? row[6].trim() : "";
                String routeTextColor = row.length > 7 ? row[7].trim() : "";

                routes.add(new Route(routeId, agencyId, routeShortName, routeLongName,
                        routeType, routeUrl, routeColor, routeTextColor));
            }
        }
        return routes;
    }

    /**
     * Parsea il file shapes.txt e restituisce la lista di Shape.
     */
    public static List<Shape> parseShapes(String filePath) throws IOException {
        List<Shape> shapes = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String shapeId = row[0].trim();
                double lat = (row.length > 1 && !row[1].trim().isEmpty()) ? Double.parseDouble(row[1].trim()) : 0.0;
                double lon = (row.length > 2 && !row[2].trim().isEmpty()) ? Double.parseDouble(row[2].trim()) : 0.0;
                int sequence = (row.length > 3 && !row[3].trim().isEmpty()) ? Integer.parseInt(row[3].trim()) : 0;
                double distTraveled = (row.length > 4 && !row[4].trim().isEmpty()) ? Double.parseDouble(row[4].trim()) : 0.0;

                shapes.add(new Shape(shapeId, lat, lon, sequence, distTraveled));
            }
        }
        return shapes;
    }

    /**
     * Parsea il file stops.txt e restituisce la lista di Stop.
     */
    public static List<Stop> parseStops(String filePath) throws IOException {
        List<Stop> stops = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String stopId = row[0].trim();
                String code = row.length > 1 ? row[1].trim() : "";
                String name = row.length > 2 ? row[2].trim() : "";
                String desc = row.length > 3 ? row[3].trim() : "";
                double lat = (row.length > 4 && !row[4].trim().isEmpty()) ? Double.parseDouble(row[4].trim()) : 0.0;
                double lon = (row.length > 5 && !row[5].trim().isEmpty()) ? Double.parseDouble(row[5].trim()) : 0.0;
                String zoneId = row.length > 6 ? row[6].trim() : "";
                int locationType = (row.length > 7 && !row[7].trim().isEmpty()) ? Integer.parseInt(row[7].trim()) : 0;
                String parentStation = row.length > 8 ? row[8].trim() : "";
                int wheelchairBoarding = (row.length > 9 && !row[9].trim().isEmpty()) ? Integer.parseInt(row[9].trim()) : 0;
                String stopUrl = row.length > 10 ? row[10].trim() : "";

                stops.add(new Stop(stopId, code, name, desc, lat, lon, zoneId,
                        locationType, parentStation, wheelchairBoarding, stopUrl));
            }
        }
        return stops;
    }

    /**
     * Parsea il file trips.txt e restituisce la lista di Trip.
     */
    public static List<Trip> parseTrips(String filePath) throws IOException {
        List<Trip> trips = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String routeId = row[0].trim();
                String serviceId = row.length > 1 ? row[1].trim() : "";
                String tripId = row.length > 2 ? row[2].trim() : "";
                String tripHeadsign = row.length > 3 ? row[3].trim() : "";
                String tripShortName = row.length > 4 ? row[4].trim() : "";
                int directionId = (row.length > 5 && !row[5].trim().isEmpty()) ? Integer.parseInt(row[5].trim()) : 0;
                String blockId = row.length > 6 ? row[6].trim() : "";
                String shapeId = row.length > 7 ? row[7].trim() : "";
                int wheelchairAccessible = (row.length > 8 && !row[8].trim().isEmpty()) ? Integer.parseInt(row[8].trim()) : 0;
                int bikesAllowed = (row.length > 9 && !row[9].trim().isEmpty()) ? Integer.parseInt(row[9].trim()) : 0;

                trips.add(new Trip(routeId, serviceId, tripId, tripHeadsign,
                        tripShortName, directionId, blockId, shapeId,
                        wheelchairAccessible, bikesAllowed));
            }
        }
        return trips;
    }

    /**
     * Parsea il file stop_times.txt e restituisce solo gli stop_times
     * che ricadono nella fascia oraria indicata.
     */
    public static List<StopTime> parseStopTimesByHour(String filePath, LocalTime oraInizio, LocalTime oraFine) throws IOException {
        List<StopTime> result = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm:ss");

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String tripId = row[0].trim();
                String arrivalTime = row.length > 1 ? row[1].trim() : "";
                String departureTime = row.length > 2 ? row[2].trim() : "";
                String stopId = row.length > 3 ? row[3].trim() : "";

                try {
                    LocalTime arr = LocalTime.parse(arrivalTime, fmt);
                    if (!arr.isBefore(oraInizio) && !arr.isAfter(oraFine)) {
                        result.add(new StopTime(tripId, arrivalTime, departureTime, stopId,
                                0, "", 0, 0, 0.0, 0));
                    }
                } catch (Exception ignored) {}
            }
        }
        return result;
    }

    /**
     * Parsea il file stop_times.txt e restituisce solo gli stop_times
     * associati a un trip specifico.
     */
    public static List<StopTime> parseStopTimesByTrip(String filePath, String tripIdTarget) throws IOException {
        List<StopTime> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // header
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] row = line.split(",", -1);

                String tripId = row[0].trim();
                if (!tripId.equals(tripIdTarget)) continue;

                String arrivalTime = row.length > 1 ? row[1].trim() : "";
                String departureTime = row.length > 2 ? row[2].trim() : "";
                String stopId = row.length > 3 ? row[3].trim() : "";

                result.add(new StopTime(tripId, arrivalTime, departureTime, stopId,
                        0, "", 0, 0, 0.0, 0));
            }
        }
        return result;
    }
}
