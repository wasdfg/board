package com.board.Question.Repository;

import com.board.Question.QuestionsImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsImageRepository extends JpaRepository<QuestionsImage, Long> {
    List<QuestionsImage> findByQuestionsUploadNumber(Integer uploadNumber);
}
