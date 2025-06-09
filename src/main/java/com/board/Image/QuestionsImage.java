package com.board.Image;

import com.board.Question.Questions;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class QuestionsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String contentType;

    private Long size;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] data;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_uploadnumber")
    private Questions questions;

    private LocalDateTime uploadedAt;

    public static QuestionsImage create(String fileName, String contentType, Long size, byte[] data, Questions questions) {
        QuestionsImage image = new QuestionsImage();
        image.setFileName(fileName);
        image.setContentType(contentType);
        image.setSize(size);
        image.setData(data);
        image.setQuestions(questions);
        image.setUploadedAt(LocalDateTime.now());
        return image;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    public void setQuestions(Questions questions) {
        this.questions = questions;
    }
}
