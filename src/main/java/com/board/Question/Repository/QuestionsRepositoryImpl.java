package com.board.Question.Repository;

import com.board.Admin.PostListDto;
import com.board.Question.Category;
import com.board.Question.Dto.QuestionsListDto;
import com.board.Question.SearchType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
                q.id, q.title, q.content, q.nowtime, q.category, q.view,
                (SELECT COUNT(r) FROM Replys r WHERE r.questions.id = q.id),
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
                        SELECT 1 FROM Replys r WHERE r.questions.id = q.id
                        AND r.content LIKE CONCAT('%' , :keyword, '%')
                    )
                """);
                case TITLE -> sb.append("q.title LIKE CONCAT('%' ,:keyword, '%')");
                case CONTENT -> sb.append("q.content LIKE CONCAT('%' ,:keyword, '%')");
                case USERNAME -> sb.append("u.nickname LIKE CONCAT('%' ,:keyword, '%')");
                case REPLYS -> sb.append("""
                    EXISTS (
                        SELECT 1 FROM Replys r WHERE r.questions.id = q.id
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

        sb.append(" ORDER BY q.id DESC");

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
                    SELECT 1 FROM Replys r WHERE r.questions.id = q.id
                    AND r.content LIKE CONCAT('%' , :keyword, '%')
                )
            """);
                case TITLE -> countSb.append("q.title LIKE CONCAT('%' ,:keyword, '%')");
                case CONTENT -> countSb.append("q.content LIKE CONCAT('%' , :keyword, '%')");
                case USERNAME -> countSb.append("u.nickname LIKE CONCAT('%' , :keyword, '%')");
                case REPLYS -> countSb.append("""
                EXISTS (
                    SELECT 1 FROM Replys r WHERE r.questions.id = q.id
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
            q.id, q.title, q.content, q.nowtime, q.category, q.view,
            (SELECT COUNT(r) FROM Replys r WHERE r.questions.id = q.id),
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

        jpql.append(" ORDER BY q.id DESC");

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


    @Override
    public Page<PostListDto> searchAdminPosts(String keyword, Category category, Boolean reported, Boolean deleted, Pageable pageable) {
        StringBuilder jpql = new StringBuilder("SELECT q.id AS id, q.title AS title, u.nickname AS writerNickname, q.createdDate AS createdDate, q.deleted AS deleted, ");
        jpql.append("(SELECT COUNT(r) > 0 FROM Report r WHERE r.question.id = q.id) AS reported ");
        jpql.append("FROM Questions q JOIN q.writer u WHERE 1=1 ");

        if (keyword != null && !keyword.isBlank()) {
            jpql.append("AND q.title LIKE :keyword ");
        }
        if (category != null) {
            jpql.append("AND q.category = :category ");
        }
        if (reported != null) {
            jpql.append("AND (SELECT COUNT(r) FROM Report r WHERE r.question.id = q.id) ");
            jpql.append(reported ? "> 0 " : "= 0 ");
        }
        if (deleted != null) {
            jpql.append("AND q.deleted = :deleted ");
        }

        String countJpql = jpql.toString().replaceFirst("SELECT .* FROM", "SELECT COUNT(q) FROM");
        Query countQuery = em.createQuery(countJpql);
        Query dataQuery = em.createQuery(jpql.toString(), Tuple.class);

        if (keyword != null && !keyword.isBlank()) {
            countQuery.setParameter("keyword", "%" + keyword + "%");
            dataQuery.setParameter("keyword", "%" + keyword + "%");
        }
        if (category != null) {
            countQuery.setParameter("category", category);
            dataQuery.setParameter("category", category);
        }
        if (deleted != null) {
            countQuery.setParameter("deleted", deleted);
            dataQuery.setParameter("deleted", deleted);
        }

        long total = (long) countQuery.getSingleResult();
        dataQuery.setFirstResult((int) pageable.getOffset());
        dataQuery.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<PostListDto> result = dataQuery.getResultList();

        return new PageImpl<>(result, pageable, total);
    }
}

