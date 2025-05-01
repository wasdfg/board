package com.board.question;

import com.board.question.dto.QuestionsListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionsRepositoryCustom {

    @Query(value = """
        SELECT 
            q.uploadnumber,
            q.title,
            q.content,
            q.nowtime,
            q.category,
            q.view,
            (
                SELECT COUNT(*) FROM replys r 
                WHERE r.questions_uploadnumber = q.uploadnumber
            ) AS replysCount,
            u.nickname
        FROM 
            questions q
        JOIN users u ON q.user_id = u.id
        WHERE 
            q.category = :category
            AND (
                :keyword IS NULL OR (
                    (:searchType = 'ALL' AND (
                        q.title LIKE CONCAT(:keyword, '%') OR
                        q.content LIKE CONCAT(:keyword, '%') OR
                        u.nickname LIKE CONCAT(:keyword, '%') OR
                        EXISTS (
                            SELECT 1 FROM replys r
                            WHERE r.questions_uploadnumber = q.uploadnumber
                            AND r.content LIKE CONCAT(:keyword, '%')
                        )
                    )) OR
                    (:searchType = 'TITLE' AND q.title LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'CONTENT' AND q.content LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'USERNAME' AND u.nickname LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'REPLYS' AND EXISTS (
                        SELECT 1 FROM replys r
                        WHERE r.questions_uploadnumber = q.uploadnumber
                        AND r.content LIKE CONCAT(:keyword, '%')
                    )) OR
                    (:searchType = 'TITLE_CONTENT' AND (
                        q.title LIKE CONCAT(:keyword, '%') OR
                        q.content LIKE CONCAT(:keyword, '%')
                    ))
                )
            )
        ORDER BY q.uploadnumber DESC
    """,
            countQuery = """
        SELECT COUNT(*) FROM questions q
        JOIN users u ON q.user_id = u.id
        WHERE 
            q.category = :category
            AND (
                :keyword IS NULL OR (
                    (:searchType = 'ALL' AND (
                        q.title LIKE CONCAT(:keyword, '%') OR
                        q.content LIKE CONCAT(:keyword, '%') OR
                        u.nickname LIKE CONCAT(:keyword, '%') OR
                        EXISTS (
                            SELECT 1 FROM replys r
                            WHERE r.questions_uploadnumber = q.uploadnumber
                            AND r.content LIKE CONCAT(:keyword, '%')
                        )
                    )) OR
                    (:searchType = 'TITLE' AND q.title LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'CONTENT' AND q.content LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'USERNAME' AND u.nickname LIKE CONCAT(:keyword, '%')) OR
                    (:searchType = 'REPLYS' AND EXISTS (
                        SELECT 1 FROM replys r
                        WHERE r.questions_uploadnumber = q.uploadnumber
                        AND r.content LIKE CONCAT(:keyword, '%')
                    )) OR
                    (:searchType = 'TITLE_CONTENT' AND (
                        q.title LIKE CONCAT(:keyword, '%') OR
                        q.content LIKE CONCAT(:keyword, '%')
                    ))
                )
            )
    """,
            nativeQuery = true)
    Page<QuestionsListDto> searchPage(@Param("category") Category category,@Param("keyword") String keyword,@Param("searchType") SearchType searchType, Pageable pageable);
}
