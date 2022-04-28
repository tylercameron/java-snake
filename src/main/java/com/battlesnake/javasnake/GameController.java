package com.battlesnake.javasnake;

import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    /**
     * For the start/end request
     */
    private static final Map<String, String> EMPTY = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    @GetMapping("/")
    public Map<String, String> index() {
        Map<String, String> response = new HashMap<>();
        response.put("apiversion", "1");
        response.put("author", "relyt");
        response.put("color", "#888888");
        response.put("head", "default");
        response.put("tail", "default");
        return response;
    }

    @PostMapping("/start")
    public Map<String, String> start() {
        LOG.info("START OF GAME");
        return EMPTY;
    }

    @PostMapping("/move")
    public Map<String, String> move(JsonNode moveRequest) {

        try {
            LOG.info("Data: {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moveRequest));
        } catch (JsonProcessingException e) {
            LOG.error("Error parsing payload", e);
        }

        /*
         * Example how to retrieve data from the request payload:
         *
         * String gameId = moveRequest.get("game").get("id").asText();
         *
         * int height = moveRequest.get("board").get("height").asInt();
         *
         */

        JsonNode head = moveRequest.get("you").get("head");
        JsonNode body = moveRequest.get("you").get("body");

        ArrayList<String> possibleMoves = new ArrayList<>(Arrays.asList("up", "down", "left", "right"));

        // Don't allow your Battlesnake to move back in on its own neck
        avoidMyNeck(head, body, possibleMoves);

        // TODO: Using information from 'moveRequest', find the edges of the board and
        // don't let your Battlesnake move beyond them board_height = ? board_width = ?

        // TODO Using information from 'moveRequest', don't let your Battlesnake pick a
        // move that would hit its own body

        // TODO: Using information from 'moveRequest', don't let your Battlesnake pick a
        // move that would collide with another Battlesnake

        // TODO: Using information from 'moveRequest', make your Battlesnake move
        // towards a piece of food on the board

        // Choose a random direction to move in
        final int choice = new Random().nextInt(possibleMoves.size());
        final String move = possibleMoves.get(choice);

        LOG.info("MOVE {}", move);

        Map<String, String> response = new HashMap<>();
        response.put("move", move);
        return response;
    }

    @PostMapping("/end")
    public Map<String, String> end() {
        LOG.info("END OF GAME");
        return EMPTY;
    }

    public void avoidMyNeck(JsonNode head, JsonNode body, ArrayList<String> possibleMoves) {
        JsonNode neck = body.get(1);

        if (neck.get("x").asInt() < head.get("x").asInt()) {
            possibleMoves.remove("left");
        } else if (neck.get("x").asInt() > head.get("x").asInt()) {
            possibleMoves.remove("right");
        } else if (neck.get("y").asInt() < head.get("y").asInt()) {
            possibleMoves.remove("down");
        } else if (neck.get("y").asInt() > head.get("y").asInt()) {
            possibleMoves.remove("up");
        }
    }
}
