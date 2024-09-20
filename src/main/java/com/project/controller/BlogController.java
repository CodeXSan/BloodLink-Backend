package com.project.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.BlogDto;
import com.project.model.Blog;
import com.project.service.BlogService;

@RestController
@RequestMapping("/blog")
public class BlogController {
	@Autowired
	private BlogService blogService;
	
	@GetMapping("/getblog")
	public List<Blog> getAllBlogs(){
		return blogService.getAllBlogs();
	}
	
	@GetMapping("/getblog/{id}")
    public ResponseEntity<Blog> getBlogById(@PathVariable String id) {
        return blogService.getBlogById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
	
	@PostMapping("/create")
	  public ResponseEntity<Blog> createBlog(@ModelAttribute BlogDto blogDto) throws IOException {
		try {
			Blog savedBlog = blogService.saveBlog(blogDto);
			return new ResponseEntity<>(savedBlog, HttpStatus.CREATED);
		} catch (IOException e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	 @GetMapping(value = "/getblog/photo/{fileName}")
	    public ResponseEntity<byte[]> getBlogPhoto(@PathVariable String fileName) {
	        try {
	            byte[] photo = blogService.getBlogPhoto(fileName);
	            return ResponseEntity.ok(photo);
	        } catch (IOException e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }
}
