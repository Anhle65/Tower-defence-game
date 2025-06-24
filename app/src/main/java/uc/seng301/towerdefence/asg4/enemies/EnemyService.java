package uc.seng301.towerdefence.asg4.enemies;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import uc.seng301.towerdefence.asg4.model.Enemy;

/**
 * Enemy API fetching functionality, makes use of {@link EnemyResponse} and Jackson lib to map the
 * returned JSON to an enemy
 */
public class EnemyService implements EnemyGenerator {
    private static final Logger LOGGER = LogManager.getLogger(EnemyService.class);
    private static final String CARD_URL =
            "https://seng.csse.canterbury.ac.nz/towerdefense/api/v1/enemies/";
    public static final int NUM_ENEMIES = 40;
    private final ObjectMapper objectMapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private final Random random;

    /**
     * Create a new enemy service
     */
    public EnemyService() {
        random = new Random();
    }

    @Override
    public Enemy getRandomEnemy() {
        int randomId = random.nextInt(NUM_ENEMIES) + 1;
        String apiResponse = getResponseFromAPI(randomId);
        if (apiResponse != null && !apiResponse.isEmpty()) {
            EnemyResponse enemyResponse = parseResponse(apiResponse);
            if (enemyResponse != null) {
                return enemyResponse.toEnemy();
            }
        }
        return getOfflineResponse().toEnemy();
    }

    /**
     * Gets the response from the API
     * 
     * @return The response body of the request as a String
     */
    private String getResponseFromAPI(int enemyId) {
        String data = null;
        try {
            LOGGER.info("Fetching enemy with id: {}", enemyId);
            URL url = new URL(CARD_URL + enemyId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int responseCode = connection.getResponseCode();
            LOGGER.info("Api responded with status code: {}", responseCode);

            if (responseCode == 200) {
                Scanner scanner = new Scanner(url.openStream());
                StringBuilder stringResult = new StringBuilder();
                while (scanner.hasNext()) {
                    stringResult.append(scanner.nextLine());
                }
                data = stringResult.toString();
                scanner.close();
            } else {
                LOGGER.error("unable to process request to API, response code is '{}'",
                        responseCode);
            }
        } catch (IOException e) {
            LOGGER.error("Error parsing API response", e);
        }
        return data;
    }

    /**
     * Parse the json response to a {@link EnemyResponse} object using Jackson
     * 
     * @param stringResult String representation of response body (JSON)
     * @return CardResponse decoded from string
     */
    private EnemyResponse parseResponse(String stringResult) {
        EnemyResponse enemyResponse = null;
        try {
            LOGGER.info(stringResult);
            enemyResponse = objectMapper.readValue(stringResult, EnemyResponse.class);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error parsing API response", e);
        }
        return enemyResponse;
    }

    /**
     * Loads enemy response from a text file if there is an issue with the API (e.g. there is no
     * internet connection, or the API is down) If there is an error loading enemies from this file
     * the application will exit as the state will be unusable
     *
     * @return {@link EnemyResponse} object with values manually assigned to those loaded from a
     *         file
     */
    private EnemyResponse getOfflineResponse() {
        LOGGER.warn("Falling back to offline enemies");
        List<EnemyResponse> enemyResponse = null;
        try {
            enemyResponse = objectMapper.readValue(
                    new File(getClass().getClassLoader().getResource("enemies.json").toURI()),
                    new TypeReference<>() {});
        } catch (URISyntaxException | IOException e) {
            LOGGER.fatal(
                    "ERROR parsing offline data, app is now exiting as no further functionality wil work",
                    e);
            System.exit(1);
        }
        return enemyResponse.get(random.nextInt(enemyResponse.size()));
    }

}
