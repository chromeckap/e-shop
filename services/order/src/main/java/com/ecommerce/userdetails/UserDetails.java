package com.ecommerce.userdetails;

import com.ecommerce.address.Address;
import com.ecommerce.order.Order;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "user_details")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @OneToOne(mappedBy = "userDetails", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Address address;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id", unique = true, nullable = false)
    private Order order;
}
