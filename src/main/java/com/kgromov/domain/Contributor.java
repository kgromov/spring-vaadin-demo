package com.kgromov.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
/*@Table(
        name="Contributor",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"login"})
)*/
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"count"})
@Builder
public class Contributor {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String login;
    private String firstName;
    private String lastName;
    private Integer count;
}
