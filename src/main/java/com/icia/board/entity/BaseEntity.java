package com.icia.board.entity;

import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public class BaseEntity {

    //작성시간
    @CreationTimestamp
    @Column(updatable = false) //업데이트땐 관여하지 말아라
    private LocalDateTime createdAt;

    //수정시간
    @CreationTimestamp
    @Column(insertable = false) //인서트 할땐 관여하지 말아라
    private LocalDateTime updatedAt;
    //작성시간과 수정시간을 갖고있음 (별도의 테이블이 만들어지는게 아니라 다른 엔티티에서 상속받아서 쓰는용도이다)
    // 상속받아지면 각각 테이블에 컬럼들이 생김(생성시간 컬럼, 수정시간 컬럼)
}
