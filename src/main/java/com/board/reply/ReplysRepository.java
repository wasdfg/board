package com.board.reply;

import com.board.reply.dto.ReplysBasicDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//JpaRepository를 상속 Reply 엔티티와 기본키인 id의 자료형
public interface ReplysRepository extends JpaRepository<Replys,Integer>{

    @Query("select new com.board.reply.dto.ReplysBasicDTO(r.content,r.questions.uploadnumber as uploadnumber,r.nowtime) from Replys r where r.author.username = :username")
    Page<ReplysBasicDTO> findByUser(@Param("username") String username, Pageable pageable);
}
//CRUD 작업을 처리하는 메서드를 내장하고 있다
