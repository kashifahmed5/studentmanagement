package net.javaguides.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.bytebuddy.dynamic.DynamicType.Builder.FieldDefinition.Optional;
import net.javaguides.sms.entity.Student;
import net.javaguides.sms.service.StudentService;

@Controller
public class StudentController {
	
	private StudentService studentService;

	public StudentController(StudentService studentService) {
		super();
		this.studentService = studentService;
	}
	
	// handler method to handle list students and return mode and view
	@GetMapping("/students")
	public String listStudents(Model model) {
		model.addAttribute("students", studentService.getAllStudents());
		return "students";
	}
	
	@GetMapping("/students/new")
	public String createStudentForm(Model model) {
		
		// create student object to hold student form data
		Student student = new Student();
		model.addAttribute("student", student);
		return "create_student";
		
	}
// 	students change if happenanything
	@PostMapping("/student")
	public String saveStudent(@ModelAttribute("student") Student student) {
		studentService.saveStudent(student);
		return "redirect:/students";
	}
	
	@PostMapping("/students")
	public long saveStudents(@RequestBody Student student) {
		studentService.saveStudent(student);
		return student.getId();

	}
	
	@GetMapping("/students/edit/{id}")
	public String editStudentForm(@PathVariable Long id, Model model) {
		model.addAttribute("student", studentService.getStudentById(id));
		return "edit_student";
	}

	@PostMapping("/students/{id}")
	public String updateStudent(@PathVariable Long id,
			@ModelAttribute("student") Student student,
			Model model) {
		
		// get student from database by id
		Student existingStudent = studentService.getStudentById(id);
		existingStudent.setId(id);
		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		existingStudent.setEmail(student.getEmail());
		
		// save updated student object
		studentService.updateStudent(existingStudent);
		return "redirect:/students";		
	}
	
//	@PutMapping("/students/{id}")
//	public long updateStudents(@PathVariable Long id,
//			@RequestBody Student student,
//			Model model) {
//		
//		// get student from database by id
//		Student existingStudent = studentService.getStudentById(id);
//		existingStudent.setId(id);
//		existingStudent.setFirstName(student.getFirstName());
//		existingStudent.setLastName(student.getLastName());
//		existingStudent.setEmail(student.getEmail());
//		
//		// save updated student object
//		studentService.updateStudent(existingStudent);
//		return student.getId();		
//	}
	@PutMapping("/students")
	@ResponseBody
	public String updateStudentss(@RequestParam Long id,
			@RequestBody Student student,
			Model model) {
		
		// get student from database by id
		Student existingStudent = studentService.getStudentById(id);
//		existingStudent.setId(id);
		existingStudent.setFirstName(student.getFirstName());
		existingStudent.setLastName(student.getLastName());
		existingStudent.setEmail(student.getEmail());
		
		// save updated student object
		studentService.updateStudent(existingStudent);
		return "redirect:/students";	
	}
	
	// handler method to handle delete student request
	@GetMapping("/students/{id}")
	public String deleteStudent(@PathVariable Long id) {
		studentService.deleteStudentById(id);
		return "redirect:/students";
	}
	
	@DeleteMapping("/students")
	@ResponseBody
	public String deleteStudents(@RequestParam(required = false) Long id) {
		studentService.deleteStudentById(id);
		return "redirect:/students";
	} 
	
	
	
	// handling delete with delete method
//	@DeleteMapping("/students/{id}")
//	public String deleteStudents(@PathVariable Long id) {
//		studentService.deleteStudentById(id);
//		return "redirect:/students";
//	}
	
}
