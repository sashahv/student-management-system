//package pl.dfjp.students.service;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.boot.test.context.SpringBootTest;
//import pl.dfjp.students.repository.student.AttachmentRepository;
//
//import java.time.LocalDate;
//
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//
//@SpringBootTest
//public class AttachmentServiceTest {
//    @InjectMocks
//    private AttachmentService attachmentService;
//
//    @Mock
//    private AttachmentRepository attachmentRepository;
//
//    @Test
//    public void should_delete_attachment_when_expired() {
//        // given
//        LocalDate expirationDate = LocalDate.now().minusYears(3);
//
//        // when
//        attachmentService.deleteExpiredAttachments();
//
//        // then
//        verify(attachmentRepository, times(1)).deleteByExpirationDateBefore(expirationDate);
//    }
//
//    @Test
//    public void should_not_delete_attachment_when_not_expired() {
//        // given
//        LocalDate expirationDate = LocalDate.now().minusYears(2).minusDays(2);
//
//        // when
//        attachmentService.deleteExpiredAttachments();
//
//        // then
//        verify(attachmentRepository, times(1)).deleteByExpirationDateBefore(expirationDate);
//    }
//}