package com.board.question;

import com.board.question.dto.QuestionsBasicDto;
import com.board.question.dto.QuestionsListDto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

//JpaRepository를 상속 Question 엔티티와 기본키인 id의 자료형
@EnableJpaRepositories
public interface QuestionsRepository extends JpaRepository<Questions,Integer>, QuestionsRepositoryCustom {

    @Query("SELECT q FROM Questions q WHERE q.users.id = :id ORDER BY q.uploadnumber desc")
    Page<QuestionsBasicDto> findByUser(@Param("id")Long id, Pageable pageable);

    @Query(value = "SELECT DISTINCT q.uploadnumber, " +
            "q.view, " +
            "q.nowtime, " +
            "q.user_id, " +
            "q.title, " +
            "COUNT(r.questions_uploadnumber) AS replysSize, " +
            "u.nickname AS nickname " +
            "FROM questions q " +
            "LEFT JOIN (SELECT id,nickname FROM users) u ON q.user_id = u.id " +
            "LEFT JOIN (SELECT questions_uploadnumber,content FROM replys) r ON q.uploadnumber = r.questions_uploadnumber " +
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
    Page<Questions> findAllList(@Param("keyword")String keyword,@Param("category") String category,Pageable pageable);

/*
    @Query(value = """
    SELECT 
        q.uploadnumber,
        q.title,
        q.content,
        q.nowtime,
        q.category,
        q.view,
        u.nickname,
        (
            SELECT COUNT(*) 
            FROM replys r 
            WHERE r.questions_uploadnumber = q.uploadnumber
        ) AS replysCount
    FROM 
        questions q
    JOIN users u ON q.user_id = u.id
    WHERE q.category = :category
    ORDER BY q.uploadnumber DESC
    """,
            countQuery = """
        SELECT COUNT(*) FROM questions q
        WHERE q.category = :category
    """,
            nativeQuery = true)
    Page<QuestionsListDto> findAllWithoutKeyword(@Param("category") Category category,Pageable pageable);*/

    @Query(value = "SELECT COUNT(DISTINCT q.uploadnumber) FROM questions q "
            + "LEFT JOIN users u ON q.user_id = u.id "
            + "WHERE (:category IS NULL OR q.category = :category)",
            nativeQuery = true)
    long countWithoutKeyword(@Param("category") Category category);

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
    Page<Questions> searchByTitle(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

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
    Page<Questions> searchByContent(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

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
    Page<Questions> searchByTitleContent(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

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
    Page<Questions> searchByReplys(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

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
    Page<Questions> searchByUsername(@Param("keyword") String keyword, @Param("category") String category, Pageable pageable);

}
// CRUD 작업을 처리하는 메서드를 내장하고 있다

