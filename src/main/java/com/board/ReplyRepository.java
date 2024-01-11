package com.board;

import org.springframework.data.jpa.repository.JpaRepository;

//JpaRepository를 상속 Reply 엔티티와 기본키인 id의 자료형
public interface ReplyRepository extends JpaRepository<Reply,Integer> {
}
//CRUD 작업을 처리하는 메서드를 내장하고 있다
