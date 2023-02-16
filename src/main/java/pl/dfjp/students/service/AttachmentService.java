package pl.dfjp.students.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.dfjp.students.entity.student.Attachment;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.student.AttachmentRepository;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final StudentRepository studentRepository;

    public AttachmentService(AttachmentRepository attachmentRepository,
                             StudentRepository studentRepository) {
        this.attachmentRepository = attachmentRepository;
        this.studentRepository = studentRepository;
    }

    public Attachment saveAttachment(Long studentId,
                                     int semester,
                                     MultipartFile file) throws Exception {

        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException("Student z indeksem " + studentId + " nie znaleziony")
        );

        String fileName = student.getName() + "_" + student.getSurname() + "_" + semester + "_sem.pdf";

        Attachment attachment = new Attachment(fileName,
                    file.getContentType(),
                    FileUtils.compressFile(file.getBytes()),
                    student);
            return attachmentRepository.save(attachment);
    }

    public void saveAttachmentsForAllStudents(HashMap<Long, MultipartFile> filesMap) throws Exception {
        for (Map.Entry<Long, MultipartFile> entry : filesMap.entrySet()) {
            Long studentId = entry.getKey();
            MultipartFile file = entry.getValue();
            Student student = studentRepository.findById(studentId).orElseThrow(
                    () -> new StudentNotFoundException("Student z indeksem " + studentId + " nie został znaleziony")
            );
            saveAttachment(studentId, student.getStudy().getActualSemester(), file);
        }
    }

    public Attachment getAttachment(String id)  {
        return attachmentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Nie istnieje pliku z id: " + id)
        );
    }

    public Attachment deleteAttachment(String id){
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Nie istnieje pliku z id: " + id)
        );
        attachmentRepository.deleteById(id);
        return attachment;
    }
}
