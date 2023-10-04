package edu.neu.csye6225.entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;


import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Data
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "uuid")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private UUID id;

    @Column(length = 20,nullable = false)
    private String firstName;

    @Column(length = 20,nullable = false)
    private String lastName;

    @Column(unique = true,nullable = false)
//    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Email provided not valid, please check the email and try again.")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,updatable = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Timestamp accountCreated;

    @Column
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Timestamp accountUpdated;

}
