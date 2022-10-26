package com.domo.account.domain;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccountUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = -6451718627829963693L;
    @Id
    @GeneratedValue
    private Long id;

    private String name;

}
