package com.austinpetrie.weatherapp;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringJoiner;

public class DarkSkyClient {

    static Logger log = Logger.getLogger(WeatherAppApplication.class.getName());
    private static RestTemplate restTemplate;
    private static ObjectMapper mapper;

    public DarkSkyClient(RestTemplate rt) {
        restTemplate = rt;
        mapper = new ObjectMapper();
    }

    public Currently getCurrentWeather(GeoCoords coords) {
        String excludedBlocks = getExcludedBlocks("minutely", "hourly", "daily", "alerts", "flags");
        String preparedApiUrl = getPreparedApiUrl(coords, excludedBlocks, -1);

        JsonNode currentlyJson = callDarkSkyApi(preparedApiUrl, "currently");

        Currently currently = null;
        try {
            currently = mapper.readValue(currentlyJson.toString(), Currently.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currently;
    }

    public Daily getFutureWeather(GeoCoords coords) {
        String excludedBlocks = getExcludedBlocks("currently", "minutely", "hourly", "alerts", "flags");
        String preparedApiUrl = getPreparedApiUrl(coords, excludedBlocks, -1);

        JsonNode dailyJson = callDarkSkyApi(preparedApiUrl, "daily");

        Daily daily = null;
        try {
            daily = mapper.readValue(dailyJson.toString(), Daily.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return daily;
    }

    public ArrayList<Daily> getPastWeather(GeoCoords coords, int numOfDaysInPast) {
        ArrayList<Daily> dailies = new ArrayList<>();

        String excludedBlocks = getExcludedBlocks("currently", "minutely", "hourly", "alerts", "flags");
        Date currentDate = new Date();
        long time = currentDate.getTime();

        for (int i = numOfDaysInPast; i > 0; i--) {
            long unixTime = (time - (i * 24 * 3600 * 1000L)) / 1000L;
            String preparedApiUrl = getPreparedApiUrl(coords, excludedBlocks, unixTime);

            JsonNode dailyJson = callDarkSkyApi(preparedApiUrl, "daily");

            try {
                Daily daily = mapper.readValue(dailyJson.toString(), Daily.class);
                dailies.add(daily);
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return dailies;
    }

    public JsonNode callDarkSkyApi(String preparedApiUrl, String block) {
        try {
            ResponseEntity<String> jsonResponse =
                    restTemplate.getForEntity(preparedApiUrl, String.class);

            if (jsonResponse.getStatusCode().equals(HttpStatus.OK)) {
                JsonNode bodyJson = mapper.readTree(jsonResponse.getBody());
                JsonNode blockJson = bodyJson.path(block);

                return blockJson;
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

    /**
     * DarkSky API returns the following blocks for each request:
     *      currently - A data point containing the weather conditions for the requested time and
     *          location. If time is omitted, it will be from the current time.
     *      minutely - A data block containing the weather conditions minute-by-minute for the next
     *          hour. If a time is specified, this block will be omitted, unless you are requesting
     *          a time within an hour of the present.
     *      hourly - A data block containing the weather conditions hour-by-hour for the next two
     *          days. If a time is specified, this block will contain data points starting at
     *          midnight (local time) of the day requested, and continuing until midnight
     *          (local time) of the following day.
     *      daily - A data block containing the weather conditions day-by-day for the next week.
     *          If a time is specified, data block will contain a single data point referring to
     *          the requested date.
     *      alerts - An alerts array, which, if present, contains any severe weather alerts
     *          pertinent to the requested location. If a time is specified, this data block will
     *          be omitted.
     *      flags - A flags object containing miscellaneous metadata about the request.
     * Excluding unneeded ones will reducing latency and saving cache space.
     */
    public String getExcludedBlocks(String... excludedBlocks) {
        StringJoiner joiner = new StringJoiner(",", "[", "]");
        for (String excludedBlock : excludedBlocks) {
            joiner.add(excludedBlock);
        }

        return joiner.toString();
    }

    public String getPreparedApiUrl(GeoCoords coords, String excludedBlocks, long time) {
        final String darkSkyUrlPrefix = "https://api.darksky.net/forecast/";

        StringBuilder preparedApiUrlBuilder = new StringBuilder();
        preparedApiUrlBuilder.append(darkSkyUrlPrefix);
        preparedApiUrlBuilder.append(Secrets.DARKSKY_API_KEY);
        preparedApiUrlBuilder.append("/" + coords.getLat() + "," + coords.getLng());
        if (time != -1) {
            preparedApiUrlBuilder.append("," + time);
        }
        preparedApiUrlBuilder.append("?");
        preparedApiUrlBuilder.append("exclude=" + excludedBlocks);

        return preparedApiUrlBuilder.toString();
    }
}
