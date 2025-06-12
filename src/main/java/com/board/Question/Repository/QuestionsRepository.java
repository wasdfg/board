package com.board.Question.Repository;

import com.board.Question.Dto.QuestionsBasicDto;
import com.board.Question.Questions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

//JpaRepository를 상속 Question 엔티티와 기본키인 id의 자료형
@EnableJpaRepositories
public interface QuestionsRepository extends JpaRepository<Questions,Integer>, QuestionsRepositoryCustom {

    @Query("SELECT q FROM Questions q WHERE q.users.id = :id ORDER BY q.uploadnumber desc")
    Page<QuestionsBasicDto> findByUser(@Param("id")Long id, Pageable pageable);

}
// CRUD 작업을 처리하는 메서드를 내장하고 있다

