package ru.practicum.mainservice.comment.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.event.model.Event;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Id;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "comment_user_owner_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "comment_event_id")
    private Event event;

    @Column(name = "comment_text")
    private String text;

    @Column(name = "comment_timestamp")
    private LocalDateTime createdOn;
}