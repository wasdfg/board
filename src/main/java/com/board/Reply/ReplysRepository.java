package com.board.Reply;

import com.board.Reply.dto.ReplysBasicDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;

import java.util.List;

//JpaRepository를 상속 Reply 엔티티와 기본키인 id의 자료형
@EnableJpaRepositories
public interface ReplysRepository extends JpaRepository<Replys,Integer>{

    @Query(value = "WITH RECURSIVE replys_tree AS (" +
            "select id,content,questions_id,user_id,nowtime,modify_Date,parent_id,depth,deleted, CAST(LPAD(id, 10, '0') AS CHAR(255)) AS path"+
            " from replys"+
            " where parent_id is null"+
            " and questions_id = :id"+
            " UNION ALL" +
            " select rp.id,rp.content,rp.questions_id,rp.user_id,rp.nowtime,rp.modify_Date,rp.parent_id,rp.depth,rp.deleted,CAST(CONCAT(replys_tree.path, '.', LPAD(rp.id, 10, '0')) AS CHAR(255)) AS path"+
            " from replys as rp"+
            " inner join replys_tree on rp.parent_id = replys_tree.id"+
            ")"+
            " select id,content,questions_id,user_id,nowtime,modify_Date,parent_id,depth,deleted,path" +
            " from replys_tree"+
            " order by path"
            , nativeQuery = true)
    List<Replys> findReplysByQuestionsId(@Param("id") Integer id);

    @Query("""
    select r.content as content,
           r.nowtime as nowtime,
           r.questions.id as questionsId 
    from Replys r 
    where r.users.id = :id
    order by questionsId desc
    """)
    Page<ReplysBasicDto> findByUser(@Param("id") Long id, Pageable pageable);
}
