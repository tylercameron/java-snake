package com.battlesnake.javasnake;

import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public Map<String, String> move(@RequestBody JsonNode moveRequest) {

//        try {
//            LOG.info("Data: {}", JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(moveRequest));
//        } catch (JsonProcessingException e) {
//            LOG.error("Error parsing payload", e);
//        }

        JsonNode head = moveRequest.get("you").get("head");
        ArrayNode body = (ArrayNode) moveRequest.get("you").get("body");

        JsonNode xMax = moveRequest.get("board").get("width");
        JsonNode yMax = moveRequest.get("board").get("height");

        ArrayList<String> possibleMoves = new ArrayList<>(Arrays.asList("up", "down", "left", "right"));

        avoidWalls(head, yMax.asInt(), xMax.asInt(), possibleMoves);
        avoidMyBody(head, body, possibleMoves);

        // TODO: there is a bug currently where in corners or perhaps at index 0's the snake will run into the walls/

        // TODO: Using information from 'moveRequest', don't let your Battlesnake pick a
        // move that would collide with another Battlesnake

        // TODO: Using information from 'moveRequest', make your Battlesnake move
        // towards a piece of food on the board

        // Choose a random direction to move in
        int choice = 0;
        if (possibleMoves.size() > 1) {
            choice = new Random().nextInt(possibleMoves.size());
        }
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

    public void avoidMyBody(JsonNode head, ArrayNode body, ArrayList<String> possibleMoves) {
        body.forEach(bodyPart -> {
            if (bodyPart.get("x").asInt() < head.get("x").asInt()) {
                possibleMoves.remove("left");
            } else if (bodyPart.get("x").asInt() > head.get("x").asInt()) {
                possibleMoves.remove("right");
            } else if (bodyPart.get("y").asInt() < head.get("y").asInt()) {
                possibleMoves.remove("down");
            } else if (bodyPart.get("y").asInt() > head.get("y").asInt()) {
                possibleMoves.remove("up");
            }
        });
    }

    public void avoidWalls(JsonNode head, int height, int width, ArrayList<String> possibleMoves) {
        int maxX = width - 1;
        int maxY = height - 1;
        int headX = head.get("x").asInt();
        int headY = head.get("y").asInt();

        if (headX == maxX) {
            possibleMoves.remove("right");
        }
        if (headX == 0) {
            possibleMoves.remove("left");
        }
        if (headY == maxY) {
            possibleMoves.remove("up");
        }
        if (headY == 0) {
            possibleMoves.remove("down");
        }
    }
}
