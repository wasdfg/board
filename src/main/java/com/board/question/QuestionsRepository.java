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

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "COUNT(r.questions_uploadnumber) AS replysSize, " +
            "u.nickname AS nickname " +
            "FROM questions q " +
            "LEFT JOIN users u ON q.user_id = u.id " +
            "LEFT JOIN replys r ON q.uploadnumber = r.questions_uploadnumber " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND ( " +
            "    :keyword IS NULL OR " +  // 키워드가 있으면 검색 조건 추가
            "    q.title LIKE CONCAT('%', :keyword, '%') OR " +
            "    q.content LIKE CONCAT('%', :keyword, '%') OR " +
            "    u.nickname LIKE CONCAT('%', :keyword, '%') OR " +
            "    r.content LIKE CONCAT('%', :keyword, '%') " +
            ") " +
            "GROUP BY q.uploadnumber " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(DISTINCT q.uploadnumber) " +
                    "FROM questions q " +
                    "LEFT JOIN users u ON q.user_id = u.id " +
                    "LEFT JOIN replys r ON q.uploadnumber = r.questions_uploadnumber " +
                    "WHERE (:category IS NULL OR q.category = :category) " +
                    "AND ( " +
                    "    :keyword IS NULL OR " +  // 키워드가 있으면 검색 조건 추가
                    "    q.title LIKE CONCAT('%', :keyword, '%') OR " +
                    "    q.content LIKE CONCAT('%', :keyword, '%') OR " +
                    "    u.nickname LIKE CONCAT('%', :keyword, '%') OR " +
                    "    r.content LIKE CONCAT('%', :keyword, '%') " +
                    ")",
            nativeQuery = true)
    Page<QuestionsListDto> findAllList(@Param("keyword")String keyword,@Param("category") String category,Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "(SELECT COUNT(*) FROM replys r WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "(SELECT u.nickname FROM users u WHERE u.id = q.user_id) AS nickname " +
            "FROM questions q " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND q.title LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(*) FROM questions q WHERE (:category IS NULL OR q.category = :category) " +
                    "AND q.title LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<QuestionsListDto> searchByTitle(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "(SELECT COUNT(*) FROM replys r WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "(SELECT u.nickname FROM users u WHERE u.id = q.user_id) AS nickname " +
            "FROM questions q " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND q.content LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(*) FROM questions q WHERE (:category IS NULL OR q.category = :category) " +
                    "AND q.content LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<QuestionsListDto> searchByContent(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "COALESCE(COUNT(r.questions_uploadnumber), 0) AS replysSize, " +
            "u.nickname " +
            "FROM questions q " +
            "LEFT JOIN users u ON u.id = q.user_id " +
            "LEFT JOIN replys r ON r.questions_uploadnumber = q.uploadnumber " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND (q.title LIKE CONCAT('%', :keyword, '%') OR q.content LIKE CONCAT('%', :keyword, '%')) " +
            "GROUP BY q.uploadnumber, q.view, q.nowtime, q.user_id, q.title, u.nickname " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(*) FROM questions q " +
                    "WHERE (:category IS NULL OR q.category = :category) " +
                    "AND (q.title LIKE CONCAT('%', :keyword, '%') OR q.content LIKE CONCAT('%', :keyword, '%'))",
            nativeQuery = true)
    Page<QuestionsListDto> searchByTitleContent(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "(SELECT COUNT(*) FROM replys r WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "(SELECT u.nickname FROM users u WHERE u.id = q.user_id) AS nickname " +
            "FROM questions q " +
            "JOIN replys r ON q.uploadnumber = r.questions_uploadnumber " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND r.content LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(*) FROM questions q " +
                    "JOIN replys r ON q.uploadnumber = r.questions_uploadnumber " +
                    "WHERE (:category IS NULL OR q.category = :category) " +
                    "AND r.content LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<QuestionsListDto> searchByReplys(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "(SELECT COUNT(*) FROM replys r WHERE r.questions_uploadnumber = q.uploadnumber) AS replysSize, " +
            "(SELECT u.nickname FROM users u WHERE u.id = q.user_id) AS nickname " +
            "FROM questions q " +
            "JOIN users u ON q.user_id = u.id " +
            "WHERE (:category IS NULL OR q.category = :category) " +
            "AND u.nickname LIKE CONCAT('%', :keyword, '%') " +
            "ORDER BY q.uploadnumber DESC",
            countQuery = "SELECT COUNT(*) FROM questions q " +
                    "JOIN users u ON q.user_id = u.id " +
                    "WHERE (:category IS NULL OR q.category = :category) " +
                    "AND u.nickname LIKE CONCAT('%', :keyword, '%')",
            nativeQuery = true)
    Page<QuestionsListDto> searchByUsername(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

}
// CRUD 작업을 처리하는 메서드를 내장하고 있다

