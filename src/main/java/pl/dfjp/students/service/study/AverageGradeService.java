package pl.dfjp.students.service.study;

import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.dfjp.students.entity.student.Student;
import pl.dfjp.students.entity.study.AverageGradeByAcademicYear;
import pl.dfjp.students.entity.study.AverageGradeBySemester;
import pl.dfjp.students.entity.study.Study;
import pl.dfjp.students.exception.StudentNotFoundException;
import pl.dfjp.students.repository.student.StudentRepository;
import pl.dfjp.students.repository.study.AverageGradeByAcademicYearRepository;
import pl.dfjp.students.repository.study.AverageGradeBySemesterRepository;
import pl.dfjp.students.repository.study.StudyRepository;
import pl.dfjp.students.service.ArchivedStudentService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class AverageGradeService {
    private final StudyRepository studyRepository;
    private final StudentRepository studentRepository;
    private final AverageGradeBySemesterRepository averageGradeBySemesterRepository;
    private final AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository;
    private final ArchivedStudentService archivedStudentService;

    @Autowired
    public AverageGradeService(StudyRepository studyRepository,
                               StudentRepository studentRepository,
                               AverageGradeBySemesterRepository averageGradeBySemesterRepository,
                               AverageGradeByAcademicYearRepository averageGradeByAcademicYearRepository,
                               ArchivedStudentService archivedStudentService) {
        this.studyRepository = studyRepository;
        this.studentRepository = studentRepository;
        this.averageGradeBySemesterRepository = averageGradeBySemesterRepository;
        this.averageGradeByAcademicYearRepository = averageGradeByAcademicYearRepository;
        this.archivedStudentService = archivedStudentService;
    }

    public void setAverageGradesForAllStudents(HashMap<Long, Double> mapping) {
        for (Map.Entry<Long, Double> entry : mapping.entrySet()) {
            Long studentId = entry.getKey();
            double grade = entry.getValue();
            generateAverageGradeForActualSemester(studentId, grade);
        }
    }

    public AverageGradeBySemester generateAverageGradeForActualSemester(Long studentId, double grade) {
        if (!(grade >= 1 && grade <= 5)) {
            throw new IllegalArgumentException("Ocena musi być w przedziale od 1 do 5");
        }
        Student student = studentRepository.findById(studentId).orElseThrow(
                () -> new StudentNotFoundException(String.format("Student z indeksem [%d] nie znaleziony", studentId))
        );
        Study study = student.getStudy();

        double roundGrade = DoubleRounder.round(grade, 3);

        AverageGradeBySemester averageGradeBySemester = setAverageGradeForActualSemester(study, roundGrade);

        if (study.getActualSemester() + 1 == 11) {
            generateAverageGradeForActualYear(study, study.getActualSemester());
            archivedStudentService.archiveStudent(student.getId());
        } else {
            increaseSemester(study);
        }
        return averageGradeBySemester;
    }

    private AverageGradeBySemester setAverageGradeForActualSemester(Study study, double roundGrade) {
        AverageGradeBySemester averageGradeBySemester = new AverageGradeBySemester();
        averageGradeBySemester.setAverageGrade(roundGrade);
        averageGradeBySemester.setSemester(study.getActualSemester()); // 1
        averageGradeBySemester.setFaculty(study.getFaculty());
        averageGradeBySemester.setFieldOfStudy(study.getFieldOfStudy());
        averageGradeBySemester.setKindOfStudy(study.getKindOfStudy());
        averageGradeBySemester.setTypeOfStudy(study.getTypeOfStudy());
        averageGradeBySemester.setStudy(study);
        averageGradeBySemesterRepository.save(averageGradeBySemester);
        return averageGradeBySemester;
    }

    public void increaseSemester(Study study) {
        study.setActualSemester(study.getActualSemester() + 1); // 2
        studyRepository.save(study);
        Integer finishedSemester = averageGradeBySemesterRepository.findBiggestSemesterByStudyId(study.getId());
        increaseAcademicYear(study, finishedSemester);
    }

    private void increaseAcademicYear(Study study, Integer finishedSemester) {
        if (study.getActualSemester() % 2 == 1) { // 1,2,[=3=],4,5,6,7,8,9,10
            generateAverageGradeForActualYear(study, finishedSemester);
            study.setYearOfStudy(study.getYearOfStudy() + 1);
            studyRepository.save(study);
        }
    }

    private void generateAverageGradeForActualYear(Study study,
                                                   int finishedSemester) {
        AverageGradeByAcademicYear averageGradeByAcademicYear = new AverageGradeByAcademicYear();
        if (finishedSemester % 2 == 0) {
            Optional<AverageGradeBySemester> avgGradeByFinishedSemester =
                    averageGradeBySemesterRepository.findBySemesterAndStudyId(finishedSemester, study.getId());

            Optional<AverageGradeBySemester> avgGradeByPreviousSemester =
                    averageGradeBySemesterRepository.findBySemesterAndStudyId(finishedSemester - 1, study.getId());

            averageGradeByAcademicYear.setFaculty(avgGradeByFinishedSemester.get().getFaculty());
            averageGradeByAcademicYear.setFieldOfStudy(avgGradeByFinishedSemester.get().getFieldOfStudy());
            averageGradeByAcademicYear.setKindOfStudy(avgGradeByFinishedSemester.get().getKindOfStudy());
            averageGradeByAcademicYear.setTypeOfStudy(avgGradeByFinishedSemester.get().getTypeOfStudy());
            averageGradeByAcademicYear.setAcademicYear(study.getYearOfStudy());

            if (avgGradeByPreviousSemester.isPresent()) {
                getResultOfCalculatingAvgGradeForYear(averageGradeByAcademicYear, avgGradeByFinishedSemester, avgGradeByPreviousSemester);
            } else {
                averageGradeByAcademicYear
                        .setAverageGrade(avgGradeByFinishedSemester.get().getAverageGrade());
            }
        }
        averageGradeByAcademicYear.setStudy(study);
        averageGradeByAcademicYearRepository.save(averageGradeByAcademicYear);
    }

    private void getResultOfCalculatingAvgGradeForYear(AverageGradeByAcademicYear averageGradeByAcademicYear,
                                                       Optional<AverageGradeBySemester> avgGradeByFinishedSemester,
                                                       Optional<AverageGradeBySemester> avgGradeByPreviousSemester) {
        double avgGradeForYear = calculateAverageGradeForActualYear(
                avgGradeByPreviousSemester.get().getAverageGrade(),
                avgGradeByFinishedSemester.get().getAverageGrade()
        );
        averageGradeByAcademicYear.setAverageGrade(avgGradeForYear);
    }

    // Set table of semester -> grade during editing student
    public void setAllGradesForStudent(HashMap<Integer, Double> mapping,
                                       Long studentId) {
        if (!mapping.isEmpty()) {
            for (Map.Entry<Integer, Double> entry : mapping.entrySet()) {
                int semester = entry.getKey();
                double grade = entry.getValue();
                setGradeForSingleSemester(studentId, semester, grade);
            }
        }
    }

    // Set grade for single semester during editing student
    private void setGradeForSingleSemester(Long studentId,
                                           int semester,
                                           double grade) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student != null) {
            Long studyId = student.getStudy().getId();
            AverageGradeBySemester averageGradeBySemester =
                    averageGradeBySemesterRepository.findBySemesterAndStudyId(semester, studyId).orElse(null);
            if (averageGradeBySemester != null) {
                averageGradeBySemester.setAverageGrade(grade);
                averageGradeBySemesterRepository.save(averageGradeBySemester);
            }
            changeAvgByYearIfSemesterAvgGradeEdited(semester, studyId, averageGradeBySemester);
        }
    }

    // If average grades for semesters was changed during editing student - avg year will automatically change its value
    private void changeAvgByYearIfSemesterAvgGradeEdited(int semester,
                                                         Long studyId,
                                                         AverageGradeBySemester averageGradeBySemester) {
        if (semester % 2 == 0) {
            AverageGradeBySemester prevSem =
                    averageGradeBySemesterRepository.findBySemesterAndStudyId(semester - 1, studyId).orElse(null);
            AverageGradeByAcademicYear avgByYear =
                    averageGradeByAcademicYearRepository.findByAcademicYearAndStudyId(semester / 2, studyId).orElse(null);

            Study study = studyRepository.findById(studyId).orElseThrow(
                    () -> new RuntimeException("Study with index " + studyId + " doesnt exists")
            );

            if (avgByYear != null) {
                avgByYear.setFaculty(
                        avgByYear.getFaculty().equals(study.getFaculty().getName())
                                ? study.getFaculty()
                                : avgByYear.getFaculty());
                avgByYear.setFieldOfStudy(
                        avgByYear.getFaculty().equals(study.getFieldOfStudy().getName())
                                ? study.getFieldOfStudy()
                                : avgByYear.getFieldOfStudy());
                avgByYear.setKindOfStudy(
                        avgByYear.getFaculty().equals(study.getKindOfStudy().getName())
                                ? study.getKindOfStudy()
                                : avgByYear.getKindOfStudy());
                avgByYear.setTypeOfStudy(
                        avgByYear.getFaculty().equals(study.getTypeOfStudy().getName())
                                ? study.getTypeOfStudy()
                                : avgByYear.getTypeOfStudy());
                if (prevSem != null) {
                    avgByYear.setAverageGrade(calculateAverageGradeForActualYear
                            (prevSem.getAverageGrade(), averageGradeBySemester.getAverageGrade()));
                } else {
                    avgByYear.setAverageGrade(averageGradeBySemesterRepository.findBySemesterAndStudyId(semester, studyId).get().getAverageGrade());
                }
                averageGradeByAcademicYearRepository.save(avgByYear);
            }
        }
    }

    private double calculateAverageGradeForActualYear(double avgFirstSem,
                                                      double avgSecondSem) {
        double averageGrade = (avgFirstSem + avgSecondSem) / 2;
        return DoubleRounder.round(averageGrade, 3);
    }
}
