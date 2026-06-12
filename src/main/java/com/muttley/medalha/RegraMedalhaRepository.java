package com.muttley.medalha;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegraMedalhaRepository extends JpaRepository<RegraMedalha, Long> {

    List<RegraMedalha> findByAtivoTrue();
}
