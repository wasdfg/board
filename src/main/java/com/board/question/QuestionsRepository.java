package com.board.question;

import com.board.question.dto.QuestionsBasicDto;
import com.board.question.dto.QuestionsListDto;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

//JpaRepository를 상속 Question 엔티티와 기본키인 id의 자료형
@EnableJpaRepositories
public interface QuestionsRepository extends JpaRepository<Questions,Integer> {
    //Questions findByUploadNumber(int num);

    Questions findByTitle(String title); //findby형식의 사용자 정의 함수
    Questions findByTitleAndContent(String title, String content);
    List<Questions> findByTitleLike(String title); //title 조회해서 찾기 값이 여러개 일 수 있으므로 list에 저장
    //@Query("select q.title,q.view,q.author,q.uploadnumber from Questions q")

    //Page<Questions> findAll(Specification<Questions> spec,Pageable pageable); //검색으로 db에서 조회한 내용을 paging해서 저장

    Page<Questions> findAll(Specification<Questions> spec, Pageable pageable);

    Page<Questions> findByCategory(Pageable pageable,@Param("category")String category);

    @Query("select new com.board.question.dto.QuestionsBasicDto(q.title,q.uploadnumber,q.nowtime) from Questions q where q.author.username = :username")
    Page<QuestionsBasicDto> findByUser(@Param("username")String username, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            " FROM questions q" +
            " WHERE (:category IS NULL OR :category = '' OR q.category = :category)",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q WHERE (:category IS NULL OR :category = '' OR q.category = :category)",
            nativeQuery = true)
    Page<QuestionsListDto> findAllList(Pageable pageable,String category);

    @Query(value = "SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            " FROM questions q WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(q.title) AGAINST(:keyword IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(q.title) AGAINST(:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<QuestionsListDto> searchByTitle(String keyword,String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title, " +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            " FROM questions q WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(q.content) AGAINST(:keyword IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(q.content) AGAINST(:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<QuestionsListDto> searchByContent(String keyword,String category, Pageable pageable);

    @Query(value = "SELECT * FROM ( " +
            "    SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize," +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            " FROM questions q " +
            "    WHERE (:category IS NULL OR :category = '' OR q.category = :category) " +
            "    AND MATCH(q.title) AGAINST(:keyword IN BOOLEAN MODE) " +
            "    UNION " +
            "    SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            "FROM questions q " +
            "    WHERE (:category IS NULL OR :category = '' OR q.category = :category) " +
            "    AND MATCH(q.content) AGAINST(:keyword IN BOOLEAN MODE) " +
            ") AS combined",
            countQuery = "SELECT COUNT(*) FROM ( " +
                    "    SELECT DISTINCT q.uploadnumber FROM questions q " +
                    "    WHERE (:category IS NULL OR :category = '' OR q.category = :category) " +
                    "    AND MATCH(q.title) AGAINST(:keyword IN BOOLEAN MODE) " +
                    "    UNION " +
                    "    SELECT DISTINCT q.uploadnumber FROM questions q " +
                    "    WHERE (:category IS NULL OR :category = '' OR q.category = :category) " +
                    "    AND MATCH(q.content) AGAINST(:keyword IN BOOLEAN MODE) " +
                    ") AS combined",
            nativeQuery = true)
    Page<QuestionsListDto> searchByTitleContent(String keyword,String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            "FROM questions q JOIN replys r ON q.uploadnumber = r.questions_uploadnumber WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(r.content) AGAINST(:keyword IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q JOIN replys r ON q.uploadnumber = r.questions_uploadnumber WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(r.content) AGAINST(:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<QuestionsListDto> searchByReplys(String keyword,String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber," +
            "    q.view," +
            "    q.nowtime," +
            "    q.user_id," +
            "    q.title," +
            "    (SELECT COUNT(*) " +
            "     FROM replys r " +
            "     WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "    (SELECT u.username " +
            "     FROM users u " +
            "     WHERE u.id = q.user_id) AS username " +
            "FROM questions q JOIN users u ON q.author_id = u.id WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(u.username) AGAINST(:keyword IN BOOLEAN MODE)",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q JOIN users u ON q.author_id = u.id WHERE (:category IS NULL OR :category = '' OR q.category = :category) AND MATCH(u.username) AGAINST(:keyword IN BOOLEAN MODE)",
            nativeQuery = true)
    Page<QuestionsListDto> searchByUsername(String keyword,String category, Pageable pageable);
    //Page<Questions> findByCategory(Pageable pageable,String category);
    //@Query("select title, author, nowtime from Questions")
    //Page<Questions> getData(Pageable pageable);
    /*@Query("select "
            + "distinct q "
            + "from Questions q "
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

