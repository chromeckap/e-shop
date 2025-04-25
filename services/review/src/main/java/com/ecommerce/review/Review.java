package com.ecommerce.review;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "review")
@EntityListeners(AuditingEntityListener.class)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1, message = "Hodnocení musí být alespoň 1.")
    @Max(value = 5, message = "Hodnocení musí být maximálně 5.")
    @NotNull(message = "Hodnocení musí být zadáno.")
    private int rating;

    @NotBlank(message = "Text recenze nesmí být prázdný.")
    private String text;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createTime;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updateDate;

    @NotNull(message = "ID uživatele nesmí být prázdné.")
    private Long userId;

    @NotNull(message = "ID produktu nesmí být prázdné.")
    private Long productId;
}
