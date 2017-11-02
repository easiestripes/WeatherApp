package com.austinpetrie.weatherapp;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class GeocodingClient {

    private static RestTemplate restTemplate;

    public GeocodingClient(RestTemplate rt) {
        restTemplate = rt;
    }

    public GeoCoords getGeoCoords(String location) {
        String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + location + "&key=" + Secrets.GEOCODING_API_KEY;

        try {
            ResponseEntity<String> jsonResponse =
                    restTemplate.getForEntity(geocodingUrl, String.class);

            if (jsonResponse.getStatusCode().equals(HttpStatus.OK)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode body = mapper.readTree(jsonResponse.getBody());

                JsonNode results = body.path("results");
                if (results.isArray()) {
                    JsonNode result = results.get(0);
                    JsonNode geometryToLocation = result.path("geometry").path("location");

                    String lat = geometryToLocation.path("lat").toString();
                    String lng = geometryToLocation.path("lng").toString();

                    return new GeoCoords(lat, lng);
                } else {
                    System.err.println("Google's Geocode API JSON format was changed");
                }
            }
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
