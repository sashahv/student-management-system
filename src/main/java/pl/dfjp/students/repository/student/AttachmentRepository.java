package pl.dfjp.students.repository.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.dfjp.students.entity.student.Attachment;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, String> {
    List<Attachment> findAllByStudentId(Long studentId);

    Attachment findByArchivedStudentId(Long id);
}
