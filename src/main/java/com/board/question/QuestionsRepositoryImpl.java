package com.board.question;

import com.board.question.dto.QuestionsListDto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public class QuestionsRepositoryImpl implements QuestionsRepositoryCustom {

    @PersistenceContext
    private EntityManager em;


    @Override
    public Page<QuestionsListDto> searchPage(Category category, String keyword, SearchType searchType, Pageable pageable) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            SELECT new com.board.question.dto.QuestionsListDtoImpl(
                q.uploadnumber, q.title, q.content, q.nowtime, q.category, q.view,
                (SELECT COUNT(r) FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber),
                u.nickname
            )
            FROM Questions q
            JOIN q.users u
            WHERE 1 = 1
        """);

        if (!"ALL".equals(category.name())) {
            sb.append(" AND q.category = :category");
        }

        if (keyword != null && !keyword.isBlank()) {
            sb.append(" AND (");
            switch (searchType) {
                case ALL -> sb.append("""
                    q.title LIKE CONCAT('%' ,:keyword, '%') OR
                    q.content LIKE CONCAT('%' ,:keyword, '%') OR
                    u.nickname LIKE CONCAT('%' ,:keyword, '%') OR
                    EXISTS (
                        SELECT 1 FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber
                        AND r.content LIKE CONCAT('%' , :keyword, '%')
                    )
                """);
                case TITLE -> sb.append("q.title LIKE CONCAT('%' ,:keyword, '%')");
                case CONTENT -> sb.append("q.content LIKE CONCAT('%' ,:keyword, '%')");
                case USERNAME -> sb.append("u.nickname LIKE CONCAT('%' ,:keyword, '%')");
                case REPLYS -> sb.append("""
                    EXISTS (
                        SELECT 1 FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber
                        AND r.content LIKE CONCAT('%' ,:keyword, '%')
                    )
                """);
                case TITLE_CONTENT -> sb.append("""
                    q.title LIKE CONCAT('%' , :keyword, '%') OR
                    q.content LIKE CONCAT('%' , :keyword, '%')
                """);
                default -> sb.append("1=1");
            }
            sb.append(")");
        }

        sb.append(" ORDER BY q.uploadnumber DESC");

        TypedQuery<QuestionsListDto> query = em.createQuery(sb.toString(), QuestionsListDto.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (!"ALL".equals(category.name())) {
            query.setParameter("category", category.name());
        }

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", keyword);
        }

        List<QuestionsListDto> resultList = query.getResultList();

        // count 쿼리
        StringBuilder countSb = new StringBuilder();
        countSb.append("SELECT COUNT(q) FROM Questions q JOIN q.users u WHERE 1 = 1");

        if (!"ALL".equals(category.name())) {
            countSb.append(" AND q.category = :category");
        }

        if (keyword != null && !keyword.isBlank()) {
            countSb.append(" AND (");
            switch (searchType) {
                case ALL -> countSb.append("""
                q.title LIKE CONCAT('%' ,:keyword, '%') OR
                q.content LIKE CONCAT('%' ,:keyword, '%') OR
                u.nickname LIKE CONCAT('%' ,:keyword, '%') OR
                EXISTS (
                    SELECT 1 FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber
                    AND r.content LIKE CONCAT('%' , :keyword, '%')
                )
            """);
                case TITLE -> countSb.append("q.title LIKE CONCAT('%' ,:keyword, '%')");
                case CONTENT -> countSb.append("q.content LIKE CONCAT('%' , :keyword, '%')");
                case USERNAME -> countSb.append("u.nickname LIKE CONCAT('%' , :keyword, '%')");
                case REPLYS -> countSb.append("""
                EXISTS (
                    SELECT 1 FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber
                    AND r.content LIKE CONCAT('%' ,:keyword, '%')
                )
            """);
                case TITLE_CONTENT -> countSb.append("""
                q.title LIKE CONCAT('%' .:keyword, '%') OR
                q.content LIKE CONCAT('%' .:keyword, '%')
            """);
            }
            countSb.append(")");
        }

        TypedQuery<Long> countQuery = em.createQuery(countSb.toString(), Long.class);

        if (!"ALL".equals(category.name())) {
            countQuery.setParameter("category", category.name());
        }
        if (keyword != null && !keyword.isBlank()) {
            countQuery.setParameter("keyword", keyword);
        }

        long total = countQuery.getSingleResult();
        return new PageImpl<>(resultList, pageable, total);
    }

    @Override
    public Page<QuestionsListDto> findAllWithoutKeyword(Category category, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("""
        SELECT new com.board.question.dto.QuestionsListDtoImpl(
            q.uploadnumber, q.title, q.content, q.nowtime, q.category, q.view,
            (SELECT COUNT(r) FROM Replys r WHERE r.questions.uploadnumber = q.uploadnumber),
            u.nickname
        )
        FROM Questions q
        JOIN q.users u
    """);

        boolean hasWhere = false;

        if (!"ALL".equals(category.name())) {
            jpql.append(" WHERE q.category = :category");
            hasWhere = true;
        }

        jpql.append(" ORDER BY q.uploadnumber DESC");

        TypedQuery<QuestionsListDto> query = em.createQuery(jpql.toString(), QuestionsListDto.class)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize());

        if (!"ALL".equals(category.name())) {
            query.setParameter("category", category.name());
        }

        List<QuestionsListDto> resultList = query.getResultList();

        // count 쿼리도 동일하게 처리
        StringBuilder countJpql = new StringBuilder("SELECT COUNT(q) FROM Questions q");

        if (!"ALL".equals(category.name())) {
            countJpql.append(" WHERE q.category = :category");
        }

        TypedQuery<Long> countQuery = em.createQuery(countJpql.toString(), Long.class);
        if (!"ALL".equals(category.name())) {
            countQuery.setParameter("category", category.name());
        }

        long total = countQuery.getSingleResult();

        return new PageImpl<>(resultList, pageable, total);
    }
}

