package com.austinpetrie.weatherapp;

import org.apache.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Date;

@RestController
public class SearchController {

    static Logger log = Logger.getLogger(WeatherAppApplication.class.getName());

    private static RestTemplate restTemplate;
    private static GeocodingClient geocodingClient;
    private static DarkSkyClient darkSkyClient;

    public SearchController() {
        restTemplate = new RestTemplate();
        geocodingClient = new GeocodingClient(restTemplate);
        darkSkyClient = new DarkSkyClient(restTemplate);
    }

    @RequestMapping(value = "/getForecast", method = RequestMethod.GET)
    public ModelAndView getForecastData(@RequestParam("location") String location, Model model) {
        GeoCoords locationCoords = geocodingClient.getGeoCoords(location);

        /*Currently currentForecast = darkSkyClient.getCurrentWeather(locationCoords);

        model.addAttribute("isDataRetrieved", true);
        model.addAttribute("location", location);
        model.addAttribute("temperature", (int) Math.round(currentForecast.getTemperature()));
        model.addAttribute("icon", currentForecast.getIcon());
        model.addAttribute("summary", currentForecast.getSummary());

        Daily weekForecast = darkSkyClient.getFutureWeather(locationCoords);
        ArrayList<DailyDataPoint> dailyForecasts = weekForecast.getData();

        ArrayList<Integer> futureTempHighs = new ArrayList<>();
        ArrayList<Integer> futureTempLows = new ArrayList<>();
        ArrayList<String> futureDayOfWeeks = new ArrayList<>();
        int numOfDaysToForecast = 3;
        // Start at 1 to skip today's data
        for (int i = 1; i <= numOfDaysToForecast; i++) {
            futureTempHighs.add((int) Math.round(dailyForecasts.get(i).getTemperatureHigh()));
            futureTempLows.add((int) Math.round(dailyForecasts.get(i).getTemperatureLow()));
            futureDayOfWeeks.add(getDayOfWeek(dailyForecasts.get(i).getTime()));
        }

        model.addAttribute("numOfDaysToForecast", numOfDaysToForecast);
        model.addAttribute("futureTempHighs", futureTempHighs);
        model.addAttribute("futureTempLows", futureTempLows);
        model.addAttribute("futureDayOfWeeks", futureDayOfWeeks);

        int numOfDaysInPast = 7;
        ArrayList<Daily> dailyPastForecasts = darkSkyClient.getPastWeather(locationCoords,
                numOfDaysInPast);
        ArrayList<DailyDataPoint> dailyPastForecastsData = new ArrayList<>();
        for (Daily dailyPastForecast : dailyPastForecasts) {
            dailyPastForecastsData.add(dailyPastForecast.getData().get(0));
        }

        ArrayList<Integer> pastTempHighs = new ArrayList<>();
        ArrayList<Integer> pastTempLows = new ArrayList<>();
        for (DailyDataPoint dailyPastForecastData : dailyPastForecastsData) {
            pastTempHighs.add((int) Math.round(dailyPastForecastData.getTemperatureHigh()));
            pastTempLows.add((int) Math.round(dailyPastForecastData.getTemperatureLow()));
        }

        model.addAttribute("pastTempHighs", pastTempHighs);
        model.addAttribute("pastTempLows", pastTempLows);
        model.addAttribute("numOfDaysInPast", numOfDaysInPast);
        model.addAttribute("startingUnixTime", dailyPastForecastsData.get(0).getTime());*/

        return new ModelAndView("index");
    }

    public static String getDayOfWeek(long time) {
        // Multiply by 1000L to account for differences between Unix and Java time
        Date d = new Date(time * 1000L);
        String date = d.toString();
        String dayOfWeek = date.substring(0, date.indexOf(" "));

        return dayOfWeek;
    }
}
