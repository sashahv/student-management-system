package pl.dfjp.students.entity.student;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Data
@NoArgsConstructor
public class Attachment {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String fileName;
    private String fileType;

    @Lob
    @Column(name = "file_data", length = 1048576)
    private byte[] data;

    @JsonIgnore
    @ManyToOne
    private Student student;

    public Attachment(String fileName, String fileType, byte[] data, Student student) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
        this.student = student;
    }
}
