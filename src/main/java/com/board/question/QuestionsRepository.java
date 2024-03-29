package com.board.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//JpaRepository를 상속 Question 엔티티와 기본키인 id의 자료형
public interface QuestionsRepository extends JpaRepository<Questions,Integer> {
    //Questions findByUploadNumber(int num);
    Questions findByTitle(String title); //findby형식의 사용자 정의 함수
    Questions findByTitleAndContent(String title, String content);
    List<Questions> findByTitleLike(String title); //title 조회해서 찾기 값이 여러개 일 수 있으므로 list에 저장

    Page<Questions> findAll(Pageable pageable);//게시판을 page단위로 출력하기 위해 만든 배열 
}
// CRUD 작업을 처리하는 메서드를 내장하고 있다