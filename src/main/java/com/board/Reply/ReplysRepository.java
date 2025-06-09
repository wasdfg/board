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
            "select uploadnumber,content,questions_uploadnumber,user_id,nowtime,modify_Date,parent_id,depth,deleted, CAST(LPAD(uploadnumber, 10, '0') AS CHAR(255)) AS path"+
            " from replys"+
            " where parent_id is null"+
            " and questions_uploadnumber = :uploadnumber"+
            " UNION ALL" +
            " select rp.uploadnumber,rp.content,rp.questions_uploadnumber,rp.user_id,rp.nowtime,rp.modify_Date,rp.parent_id,rp.depth,rp.deleted,CAST(CONCAT(replys_tree.path, '.', LPAD(rp.uploadnumber, 10, '0')) AS CHAR(255)) AS path"+
            " from replys as rp"+
            " inner join replys_tree on rp.parent_id = replys_tree.uploadnumber"+
            ")"+
            " select uploadnumber,content,questions_uploadnumber,user_id,nowtime,modify_Date,parent_id,depth,deleted,path" +
            " from replys_tree"+
            " order by path"
            , nativeQuery = true)
    List<Replys> findReplysByQuestionsUploadnumber(@Param("uploadnumber") Integer uploadnumber);

    @Query("""
    select r.content as content,
           r.nowtime as nowtime,
           r.questions.uploadnumber as questionsUploadnumber 
    from Replys r 
    where r.users.id = :id
    order by questionsUploadnumber desc
    """)
    Page<ReplysBasicDto> findByUser(@Param("id") Long id, Pageable pageable);
}
