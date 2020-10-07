package com.mfi.test;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Logger;

/**
 * A random class that uses random.org APIs.
 */
public class Random {
    /**
     * FIXME WARNING: Put your own API key here!
     */
    private static final String API_KEY = "YourApiKeyHere";
    /**
     * The API url from random.org.
     */
    private static final String RANDOM_API_URL = "https://api.random.org/json-rpc/2/invoke";
    /**
     * The method used on random.org to get an integer value.
     */
    private static final String CALLED_API_METHOD = "generateIntegers";
    /**
     * To indicate how many random numbers we ask to the API.
     */
    private static final int NUMBER_OF_ASKED_NUMBERS = 1;
    /**
     * To indicate the minimum random value that API must answer.
     */
    private static final int MIN_RANDOM_VALUE = 0;
    /**
     * To indicate the maximum random value that API must answer.
     */
    private static final int MAX_RANDOM_VALUE = 4;
    /**
     * The HTTP method used to call the API. Must be POST as asked by API.
     */
    private static final String USED_REQUEST_METHOD = "POST";
    /**
     * Content type must be in request header as asked by API and must be JSON content type.
     */
    private static final String JSON_CONTENT_TYPE = "application/json";
    /**
     * A logger for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Random.class.getName());

    /**
     * Get a random integer from a web API.
     *
     * @return A random integer between 0 and 4. -1 is returned if something went wrong.
     */
    public static int getRandomInt() {
        int returnedResult = -1;

        JSONObject params = new JSONObject();
        params.put("apiKey", API_KEY);
        params.put("n", NUMBER_OF_ASKED_NUMBERS);
        params.put("min", MIN_RANDOM_VALUE);
        params.put("max", MAX_RANDOM_VALUE);

        JSONObject body = new JSONObject();
        // Mandatory and fixed.
        body.put("jsonrpc", "2.0");
        body.put("method", CALLED_API_METHOD);
        // We won't use the ID because we don't need to check the response order. So we don't really care. But it's
        // mandatory for the API.
        body.put("id", "12345");
        body.put("params", params);

        try {
            HttpURLConnection connection = (HttpURLConnection) URI.create(RANDOM_API_URL).toURL().openConnection();
            connection.setRequestProperty("Content-type", JSON_CONTENT_TYPE);
            connection.setRequestMethod(USED_REQUEST_METHOD);

            // Writing the JSON body inside the request.
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            os.write(body.toJSONString().getBytes());
            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                try {
                    JSONObject jsonResponse = (JSONObject) new JSONParser().parse(response.toString());
                    JSONObject result = (JSONObject) jsonResponse.get("result");

                    // If there's no result, that means that something went wrong.
                    if (result != null) {
                        Long randomNumber = (Long) ((JSONArray) ((JSONObject) result.get("random")).get("data")).get(0);
                        returnedResult = randomNumber.intValue();
                    } else {
                        LOGGER.warning("There's no result in the JSON. An error has probably occured.");
                        LOGGER.warning(jsonResponse.toJSONString());
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                LOGGER.warning("HTTP response is not OK.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return returnedResult;
    }
}
