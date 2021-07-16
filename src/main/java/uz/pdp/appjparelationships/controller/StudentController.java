package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId, @RequestParam int page){
        Pageable pageable=PageRequest.of(page, 10);
        Page<Student> studentPage=studentRepository.findAllByGroup_FacultyId(facultyId,pageable);
        return studentPage;
    }

    //4. GROUP OWNER
    //Guruhda ko'p ham talaba bo'lmagani uchun list qilib qayatarib qo'ya qoldim
    @GetMapping("/forGroup/{groupId}")
    public List<Student> getStudentByGroupId(@PathVariable Integer groupId){
        return studentRepository.findAllByGroupId(groupId);
    }

    //Create
    @RequestMapping(value = "/student", method = RequestMethod.POST)
    public String addStudent(StudentDto studentDto) {
        Optional<Group> optionalGroups = groupRepository.findById(studentDto.getGroupId());
        if (!optionalGroups.isPresent()) {
            return "Group not found";
        }
        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectsId());
        Student student = new Student();
        student.setFirstName(studentDto.getFirstName());
        student.setLastName(studentDto.getLastName());
        student.setAge(studentDto.getAge());
        optionalGroups.ifPresent(student::setGroup);
        student.setSubjects(subjectList.subList(0, subjectList.size()));
        studentRepository.save(student);
        return "Successfully added";
    }


    //Update
    @RequestMapping(value = "/student/{id}", method = RequestMethod.PUT)
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Group> optionalGroups = groupRepository.findById(studentDto.getGroupId());
        List<Subject> subjectList = subjectRepository.findAllById(studentDto.getSubjectsId());

        if (optionalStudent.isPresent()) {
            Student editingStudent = optionalStudent.get();
            Group groups = editingStudent.getGroup();
            List<Subject> subject = editingStudent.getSubjects();
            editingStudent.setFirstName(studentDto.getFirstName());
            editingStudent.setLastName(studentDto.getLastName());
            editingStudent.setAge(studentDto.getAge());
            optionalGroups.ifPresent(editingStudent::setGroup);
            editingStudent.setSubjects(subjectList.subList(0, subjectList.size()));
            groupRepository.save(groups);
            subjectRepository.save(subject.listIterator().next());
            studentRepository.save(editingStudent);
            return "Successfully edited";
        }
        return "Student not found";
    }

    // Delete
    @RequestMapping(value = "/student/{id}", method = RequestMethod.DELETE)
    public String deleteStudent(@PathVariable Integer id) {
        studentRepository.deleteById(id);
        boolean deleted = studentRepository.existsById(id);
        if (deleted) {
            return "Successfully deleted";
        } else {
            return "Student not found";
        }
    }




}
