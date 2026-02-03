package org.son.sonstudy.domain.user.model;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.son.sonstudy.domain.product.model.ProductLike;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @Tsid
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column
    private String address;   // TO DO: address는 언제 받는게 좋을까요. 유저 생성 때? 첫 구매하기 전?

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductLike> likes = new ArrayList<>();

    @Builder
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void approveSeller() {
        this.role = Role.SELLER;
    }

    public void addLike(ProductLike like) {
        if (!this.likes.contains(like)) {
            this.likes.add(like);
        }
    }

    public void removeLike(ProductLike like) {
        this.likes.remove(like);
    }
}
