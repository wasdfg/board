package com.board.Question;

import com.board.Image.QuestionsImage;
import com.board.Reply.Replys;
import com.board.User.Users;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor //기본 생성자 자동추가 public Question{}을 생성해준다
@Getter
@Entity //데이터베이스 entity로 사용하기 위해 선언
public class Questions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //generationtype은 값을 자동으로 1씩 증가시켜준다
    private Integer uploadnumber; //int와 달리 integer는 null값으로 사용이 가능하여 sql용으로 적절하다. 저장공간도 크다
    
    @Column(length = 200,nullable = false) //길이를 200으로 제한
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)//해당 컬럼을 TEXT로 정의 하겠다
    private String content;

    private LocalDateTime nowtime;

    private LocalDateTime modifyDate; //수정된 글의 시간

    @OneToMany(mappedBy = "questions",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY) //질문이 삭제되면 답변도 같이 삭제되게 cascade를 추가
    private List<Replys> replysList = new ArrayList<>(); //reply 객체를 list로 담아둔다.
    //참조하기 위해 questions.getReplysList()를 호출한다.
    //mappedBy는 참조하는 엔티티를 선언해준다.
    
    //이미지를 위한 양방향
    @OneToMany(mappedBy = "questions", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuestionsImage> images = new ArrayList<>();

    @ManyToOne //글쓴이 1명당 여러 질문을 할 수 있어 다대1로 설정
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private Users users;

    @ManyToMany
    @JoinTable(
            name = "questions_voter", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "questions_uploadnumber"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private Set<Users> voter = new HashSet<>();

    @Column(columnDefinition = "integer default 0", nullable = false) //0부터 시작하는 조회수
    private int view;

    @Column(nullable = false)
    private String category;

    public static Questions create(String title, String content, Users users,Category category){
        Questions q = new Questions();
        q.setTitle(title);
        q.setContent(content);
        q.setNowtime(LocalDateTime.now());
        q.setUsers(users);
        q.setCategory(category.getValue());
        return q;
    }

    public void modify(String title,String content){ //수정할 내용 저장
        this.title = title;
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void increaseView() {
        this.view++;
    }

    public void setModifyDate(LocalDateTime time) {
        this.modifyDate = time;
    }

    public void setNowtime(LocalDateTime time){
        this.nowtime = time;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public void setVoter(Set<Users> voter) {
        this.voter = voter;
    }

    public void setReplysList(List<Replys> replysList) {
        this.replysList = replysList;
    }
}
