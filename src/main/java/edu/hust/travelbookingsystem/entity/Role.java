package edu.hust.travelbookingsystem.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.hust.travelbookingsystem.enums.RoleCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "roles")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Enumerated(EnumType.STRING) // lưu db ở dạng chuỗi
    @Column(nullable = false)
    private RoleCode roleCode;

    @OneToMany(mappedBy = "role" , cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    public Role(RoleCode roleCode) {
        this.roleCode = roleCode;
    }

}
