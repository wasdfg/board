package com.board.question;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
//JpaRepository를 상속 Question 엔티티와 기본키인 id의 자료형
public interface QuestionsRepository extends JpaRepository<Questions,Integer> {
    //Questions findByUploadNumber(int num);

    Questions findByTitle(String title); //findby형식의 사용자 정의 함수
    Questions findByTitleAndContent(String title, String content);
    List<Questions> findByTitleLike(String title); //title 조회해서 찾기 값이 여러개 일 수 있으므로 list에 저장

    Page<Questions> findAll(Specification<Questions> spec,Pageable pageable); //검색으로 db에서 조회한 내용을 paging해서 저장

    Page<Questions> findByCategory(Pageable pageable,String category);
    //@Query("select title, author, nowtime from questions")
    //Page<Questions> getData(Pageable pageable);
    /*@Query("select "
            + "distinct q "
            + "from Question q "
            + "left outer join SignUpUser u1 on q.author=u1 "
            + "left outer join replys a on a.question=q "
            + "left outer join SignUpUser u2 on a.author=u2 "
            + "where "
            + "   q.subject like %:kw% "
            + "   or q.content like %:kw% "
            + "   or u1.username like %:kw% "
            + "   or a.content like %:kw% "
            + "   or u2.username like %:kw% ")
    Page<Questions> findAllByKeyword(@Param("kw") String kw, Pageable pageable);*/ //쿼리로 직접 선언 가능
}
// CRUD 작업을 처리하는 메서드를 내장하고 있다

