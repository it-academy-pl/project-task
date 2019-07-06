package pl.itacademy.schedule.holidays;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import pl.itacademy.schedule.util.PropertiesReader;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HolidaysWebClient {

    public static void main(String[] args) throws InterruptedException {
        HolidaysWebClient webClient = new HolidaysWebClient();
        System.out.println(webClient.getHolidays(2019));
        Thread.sleep(1100);
        System.out.println(webClient.getHolidays(2020));
    }

    public List<LocalDate> getHolidays(int year) {

        PropertiesReader propertiesReader = PropertiesReader.getInstance();
        String url = propertiesReader.readProperty("calendarific.url");
        String holidaysEndpoint = propertiesReader.readProperty("calendarific.holidays.endpoint");
        String country = propertiesReader.readProperty("calendarific.country");
        String type = propertiesReader.readProperty("calendarific.type");
        String key = propertiesReader.readProperty("calendarific.api.key");
        String jsonPath = propertiesReader.readProperty("calendarific.jsonPath");

        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target(url)
                .path(holidaysEndpoint)
                .queryParam("country", country)
                .queryParam("type", type)
                .queryParam("api_key", key)
                .queryParam("year", year);
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);

        String json = invocationBuilder.get(String.class);

        DocumentContext context = JsonPath.parse(json);
        JSONArray holidays = context.read(jsonPath);

        return holidays.stream()
                .map(holiday -> LocalDate.parse((String) holiday, DateTimeFormatter.ISO_DATE))
                .collect(Collectors.toList());
    }
    
    public List<LocalDate> getHolidaysFromEnrico(LocalDate startDate, LocalDate endDate) {

        Client client = ClientBuilder.newClient();
        PropertiesReader properties=PropertiesReader.getInstance();
        String url = properties.readProperty("enrico.url");
        String action = properties.readProperty("enrico.action");
        String country = properties.readProperty("enrico.country");
        String type = properties.readProperty("enrico.type");
        String jsonPath = properties.readProperty("enrico.jsonPath");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(properties.readProperty("enrico.dateFormat"));
        
        WebTarget webTarget = client.target(url)
                .queryParam("action", action)
                .queryParam("country", country)
                .queryParam("type", type)
                .queryParam("fromDate", startDate.format(dateFormatter))
        		.queryParam("toDate", endDate.format(dateFormatter));
        
        Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
        String json = invocationBuilder.get(String.class);

//        try {
//			Files.write(Paths.get("receivedJSON.json"),json.getBytes());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		List<Map<String,Integer>> holidays = JsonPath.parse(json).read(jsonPath);

		return holidays.stream()
				.map(holiday->LocalDate.of(holiday.get("year"),holiday.get("month"),holiday.get("day")))
				.collect(Collectors.toList());
    }
}
