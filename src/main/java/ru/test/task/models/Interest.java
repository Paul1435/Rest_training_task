package ru.test.task.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "interests")
public class Interest {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @Column(name = "label")
    @NotEmpty(message = "label can't be empty")
    private String label;


    @ManyToMany(mappedBy = "interests",
            fetch = FetchType.LAZY,
            cascade = CascadeType.REMOVE
    )
    @JsonIgnore
    private List<User> users;

    @Override
    public String toString() {
        return "Interest{" +
                "id=" + id +
                ", label='" + label + '\'' +
                '}';
    }
}
