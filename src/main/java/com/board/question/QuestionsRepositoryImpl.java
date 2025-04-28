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
    private EntityManager entityManager;

    @Override
    public Page<QuestionsListDto> searchPage(Category category,String keyword,SearchType searchType,Pageable pageable) {
        StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT DISTINCT new com.board.question.dto.QuestionsListDto(" +
                "q.uploadnumber, q.title, q.content, q.nowtime, q.category, q.view, " +
                "SIZE(q.replysList), u.nickname, null) " +
                "FROM Questions q ");

        // 필요한 경우에만 댓글 JOIN
        if (searchType == SearchType.REPLYS || searchType == SearchType.ALL) {
            jpql.append("LEFT JOIN q.replysList r ");
        }

        // users는 항상 조인 (nickname 검색하려면 필요)
        jpql.append("JOIN q.users u ");

        jpql.append("WHERE 1=1 ");

        if (category != null && category != Category.ALL) {
            jpql.append("AND q.category = :category ");
        }

        if (keyword != null && !keyword.isBlank()) {
            keyword = keyword + "%";
            switch (searchType) {
                case TITLE:
                    jpql.append("AND q.title LIKE :keyword ");
                    break;
                case CONTENT:
                    jpql.append("AND q.content LIKE :keyword ");
                    break;
                case TITLE_CONTENT:
                    jpql.append("AND (q.title LIKE :keyword OR q.content LIKE :keyword) ");
                    break;
                case USERNAME:
                    jpql.append("AND u.nickname LIKE :keyword ");
                    break;
                case REPLYS:
                    jpql.append("AND r.content LIKE :keyword ");
                    break;
                case ALL:
                    jpql.append("AND (q.title LIKE :keyword OR q.content LIKE :keyword " +
                            "OR u.nickname LIKE :keyword OR r.content LIKE :keyword) ");
                    break;
            }
        }
        jpql.append("ORDER BY q.uploadnumber DESC ");

        TypedQuery<QuestionsListDto> query = entityManager.createQuery(jpql.toString(), QuestionsListDto.class);

        if (keyword != null && !keyword.isBlank()) {
            query.setParameter("keyword", keyword);
        }
        if (category != null && category != Category.ALL) {
            query.setParameter("category", category.getValue());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<QuestionsListDto> content = query.getResultList();

        // Count 쿼리
        StringBuilder countJpql = new StringBuilder();
        countJpql.append("SELECT COUNT(DISTINCT q) FROM Questions q ");

        if (searchType == SearchType.REPLYS || searchType == SearchType.ALL) {
            countJpql.append("LEFT JOIN q.replysList r ");
        }

        countJpql.append("JOIN q.users u "); // users 닉네임 검색 대비

        countJpql.append("WHERE 1=1 ");

        if (category != null && category != Category.ALL) {
            countJpql.append("AND q.category = :category ");
        }

        if (keyword != null && !keyword.isBlank()) {
            switch (searchType) {
                case TITLE:
                    countJpql.append("AND q.title LIKE :keyword ");
                    break;
                case CONTENT:
                    countJpql.append("AND q.content LIKE :keyword ");
                    break;
                case TITLE_CONTENT:
                    countJpql.append("AND (q.title LIKE :keyword OR q.content LIKE :keyword) ");
                    break;
                case USERNAME:
                    countJpql.append("AND u.nickname LIKE :keyword ");
                    break;
                case REPLYS:
                    countJpql.append("AND r.content LIKE :keyword ");
                    break;
                case ALL:
                    countJpql.append("AND (q.title LIKE :keyword OR q.content LIKE :keyword " +
                            "OR u.nickname LIKE :keyword OR r.content LIKE :keyword) ");
                    break;
            }
        }

        TypedQuery<Long> countQuery = entityManager.createQuery(countJpql.toString(), Long.class);
        if (keyword != null && !keyword.isBlank()) {
            countQuery.setParameter("keyword", keyword);
        }
        if (category != null && category != Category.ALL) {
            countQuery.setParameter("category", category.getValue());
        }

        long total = countQuery.getSingleResult();

        return new PageImpl<>(content, pageable, total);
    }

}

