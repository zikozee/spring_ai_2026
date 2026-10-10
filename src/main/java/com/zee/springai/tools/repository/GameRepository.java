package com.zee.springai.tools.repository;


import com.zee.springai.tools.dto.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;


/**
 * @dev : Ezekiel Eromosei
 * @date : 10 Oct, 2026
 */

public interface GameRepository  extends CrudRepository<Game, Long> {

    Optional<Game> findBySlug(String slug);
}
