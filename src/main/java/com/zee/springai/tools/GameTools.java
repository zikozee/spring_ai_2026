package com.zee.springai.tools;


import com.zee.springai.tools.datatype.GameComplexity;
import com.zee.springai.tools.dto.Game;
import com.zee.springai.tools.dto.GameComplexityResponse;
import com.zee.springai.tools.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @dev : Ezekiel Eromosei
 * @date : 10 Oct, 2026
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class GameTools {

    private final GameRepository gameRepository;

    @Tool(name = "getGameComplexity",
            description = "Returns a game's complexity/difficulty given the game's title/name.")
    public GameComplexityResponse getGameComplexity(@ToolParam(description = "The title of the game") String gameTitle){
        String gameSlug = gameTitle.toLowerCase().replace(" ", "_");

        log.info("Getting gameComplexity for title {}, ({})", gameTitle, gameSlug);

        Optional<Game> optionalGame = gameRepository.findBySlug(gameSlug);
        Game game = optionalGame.orElseGet(() -> {
            log.warn("Game not found: {}", gameSlug);
            return new Game(null, gameSlug, gameTitle, GameComplexity.UNKNOWN.getValue());
        });

        return new GameComplexityResponse(game.title(), game.complexityEnum());
    }
}
