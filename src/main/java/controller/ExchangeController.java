package controller;

import net.minidev.json.parser.ParseException;
import org.json.*;
import net.minidev.json.parser.JSONParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

@RestController
public class ExchangeController {

    // App Id - 08f1861f20c248c09ee0a7f6daa9be5f
    // Latest - https://openexchangerates.org/api/latest.json?app_id=08f1861f20c248c09ee0a7f6daa9be5f&symbols=Rub
    // Yesterday -https://openexchangerates.org/api/historical/<DATE>.json?app_id=08f1861f20c248c09ee0a7f6daa9be5f&symbols=rub

    @GetMapping("/")
    public String ex(Model model) {
        model.addAttribute("url", giphy());

        return "exchange";
    }

    public boolean exchange() {
        float[] array = new float[2];
        boolean richOrNot = false;
        try {
            for (int i = 0; i < 2; i++) {
                URL url = new URL("https://openexchangerates.org/api/historical/" + getDate(i) + ".json?app_id=08f1861f20c248c09ee0a7f6daa9be5f&symbols=rub");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                int response = connection.getResponseCode();
                if (response != 200) {
                    throw new RuntimeException(String.valueOf(response));
                } else {
                    Scanner scanner = new Scanner(url.openStream());
                    String informationString = new String();
                    while (scanner.hasNext()) {
                        informationString += scanner.nextLine();
                    }
                    scanner.close();

                    // JSON parse
                    JSONObject data = new JSONObject(informationString);
                    float test = data.getJSONObject("rates").getFloat("RUB");

                    array[i] = test;
                    //System.out.println(array[i] + "\n");
                }
            }
            if (array[0] > array[1]) {
                richOrNot = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return richOrNot;
    }

    public String giphy() {
        String search;
        if (exchange()) {
            search = "rich";
        } else {
            search = "broke";
        }
        try {
            URL url = new URL("https://api.giphy.com/v1/gifs/search?q=" + search + "&api_key=LK3ammJxxYLoeGmgCTN6BeZWBr9Ss64n&limit=5");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int response = connection.getResponseCode();
            if (response != 200) {
                throw new RuntimeException(String.valueOf(response));
            } else {
                Scanner scanner = new Scanner(url.openStream());
                String informationString = new String();
                while (scanner.hasNext()) {
                    informationString += scanner.nextLine();
                }
                scanner.close();

                // JSON parse
                int rnd = (int) (Math.random() * (5 - 0) + 0);
                JSONObject data = new JSONObject(informationString);
                String test = data.getJSONArray("data")
                        .getJSONObject(rnd)
                        .getJSONObject("images")
                        .getJSONObject("original")
                        .getString("url");

//            System.out.println(test + "\n");
                return test;
            }

        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDate(int x) {
        LocalDate today = LocalDate.now();
        String neededDay = (today.minusDays(x)).format(DateTimeFormatter.ISO_DATE);
        return neededDay;
    }
}
