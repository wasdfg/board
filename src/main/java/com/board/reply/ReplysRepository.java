package com.board.reply;

import com.board.reply.dto.ReplysBasicDto;
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
            "select uploadnumber,content,questions_uploadnumber,author_id,nowtime,modify_Date,parent_id,depth, CAST(LPAD(uploadnumber, 10, '0') AS CHAR(255)) AS path"+
            " from replys"+
            " where parent_id is null"+
            " and questions_uploadnumber = :uploadnumber"+
            " UNION ALL" +
            " select rp.uploadnumber,rp.content,rp.questions_uploadnumber,rp.author_id,rp.nowtime,rp.modify_Date,rp.parent_id,rp.depth,CAST(CONCAT(replys_tree.path, '.', LPAD(rp.uploadnumber, 10, '0')) AS CHAR(255)) AS path"+
            " from replys as rp"+
            " inner join replys_tree on rp.parent_id = replys_tree.uploadnumber"+
            ")"+
            " select uploadnumber,content,questions_uploadnumber,author_id,nowtime,modify_Date,parent_id,depth,path" +
            " from replys_tree"+
            " order by path"
            , nativeQuery = true) //속도향상을 위해 네이티브 쿼리를 사용해봄
    List<Replys> findReplysByQuestionsUploadnumber(@Param("uploadnumber") Integer uploadnumber);

    @Query("select new com.board.reply.dto.ReplysBasicDto(r.content,r.questions.uploadnumber as uploadnumber,r.nowtime) from Replys r where r.author.username = :username")
    Page<ReplysBasicDto> findByUser(@Param("username") String username, Pageable pageable);
}
//CRUD 작업을 처리하는 메서드를 내장하고 있다
