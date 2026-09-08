package me.zhulin.shopapi.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * Created By Zhu Lin on 3/12/2018.
 */
@Entity
@Data
@Table(name = "users")
@NoArgsConstructor
public class User implements Serializable {

    private static final long serialVersionUID = 4887904943282174032L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NaturalId
    @NotEmpty
    @Email
    @Size(min = 6, max = 50, message = "Email must be between 6 and 50 characters")
    private String email;

    @NotEmpty
    @Size(min = 3, max = 20, message = "Password must be between 3 and 20 characters", groups = me.zhulin.shopapi.validation.OnCreate.class)
    @Column(columnDefinition = "varchar(100)")
    private String password;

    @NotEmpty
    private String name;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String address;

    private boolean active = true;

    // Không @NotEmpty — Service ép ROLE_CUSTOMER
    private String role = "ROLE_CUSTOMER";

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Cart cart;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", active=" + active +
                ", role='" + role + '\'' +
                '}';
    }
}