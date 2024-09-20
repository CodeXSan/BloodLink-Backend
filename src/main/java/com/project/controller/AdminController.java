package com.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.project.dto.BlogDto;
import com.project.model.Blog;
import com.project.model.BloodRequest;
import com.project.model.Donor;
import com.project.repository.BloodRequestRepository;
import com.project.repository.DonorRepository;
import com.project.service.BlogService;
import com.project.service.BloodRequestService;
import com.project.service.DonorService;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {
	 @Autowired
	 private DonorRepository donorRepository;
	 @Autowired
	 private DonorService donorService;
	 @Autowired
	 private BlogService blogService;
	 @Autowired
	 private BloodRequestRepository requestRepository;
	 @Autowired
	 private BloodRequestService bloodRequestService;

	 @GetMapping("/getdonors")
	 public ResponseEntity<List<Donor>> getAllDonors() {
		 List<Donor> donors = donorRepository.findAll();
		 return new ResponseEntity<>(donors, HttpStatus.OK);
		 }
	 @GetMapping("/search")
	 public List<Donor> searchDonors(@RequestParam(value = "query", required = false) String searchQuery) {
	        return donorService.searchDonors(
	                searchQuery != null ? searchQuery : ""
	        );
	    }
	 @DeleteMapping("/blog/delete/{id}")
	 public ResponseEntity<?> deleteBlog(@PathVariable String id) {
	     try {
	         blogService.deleteBlog(id);
	         return ResponseEntity.ok().build();
	     } catch (Exception e) {
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	     }
	 }
	 @PostMapping("/blog/create")
	  public ResponseEntity<Blog> createBlog(@ModelAttribute BlogDto blogDto) throws IOException {
		try {
			Blog savedBlog = blogService.saveBlog(blogDto);
			return new ResponseEntity<>(savedBlog, HttpStatus.CREATED);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	 @PutMapping("/blog/edit/{id}")
	 public ResponseEntity<?> editBlog(
	            @PathVariable String id,
	            @RequestParam String title,
	            @RequestParam String content,
	            @RequestParam(required = false) MultipartFile photo) throws IOException {
	        Blog updatedBlog = blogService.updateBlog(id, title, content, photo);
	        return ResponseEntity.ok(updatedBlog);
	    }
	 @GetMapping("/newdonors")
	 public ResponseEntity<Map<String, Integer>> getNewDonorsToday() {
		    LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		    LocalDateTime endOfDay = startOfDay.plusDays(1);

		    long count = donorRepository.countByCreatedDateBetween(startOfDay, endOfDay);

		    Map<String, Integer> response = new HashMap<>();
		    response.put("count", (int) count);

		    return new ResponseEntity<>(response, HttpStatus.OK);
		}
	 @GetMapping("/newrequests")
	    public ResponseEntity<Map<String, Integer>> getNewRequestsToday() {
	        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
	        LocalDateTime endOfDay = startOfDay.plusDays(1);

	        long count = requestRepository.countByRequestDateBetween(startOfDay, endOfDay);

	        Map<String, Integer> response = new HashMap<>();
	        response.put("count", (int) count);

	        return new ResponseEntity<>(response, HttpStatus.OK);
	    }
	 @GetMapping("/pending")
	    public ResponseEntity<List<BloodRequest>> getPendingRequests() {
	        List<BloodRequest> requests = bloodRequestService.getPendingRequests();
	        return ResponseEntity.ok(requests);
	    }

	    @GetMapping("/accepted")
	    public ResponseEntity<List<BloodRequest>> getAcceptedRequests() {
	        List<BloodRequest> requests = bloodRequestService.getAcceptedRequests();
	        return ResponseEntity.ok(requests);
	    }
	   
}
