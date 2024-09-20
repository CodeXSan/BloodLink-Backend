package com.project.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.project.dto.BlogDto;
import com.project.model.Blog;
import com.project.repository.BlogRepository;

@Service
public class BlogService {
	@Autowired
	private BlogRepository blogRepository;
	@Autowired
	private PictureUploadService pictureUploadService;
	public List<Blog> getAllBlogs(){
		return blogRepository.findAll();
	}
	
	public Optional<Blog> getBlogById(String id) {
        return blogRepository.findById(id);
    }
	
	public Blog saveBlog(BlogDto blogDto) throws IOException {
		String fileName = pictureUploadService.uploadProfilePicture(blogDto.getFile());
		Blog blog = new Blog();
		blog.setTitle(blogDto.getTitle());
		blog.setContent(blogDto.getContent());
		blog.setAuthor("Admin");
		blog.setPhotoUrl(fileName);
		blog.setCreatedDate(LocalDate.now());
		return blogRepository.save(blog);
	}
	
	public byte[] getBlogPhoto(String fileName) throws IOException {
        return pictureUploadService.getProfilePicture(fileName);
    }
	
	public void deleteBlog(String id) {
        blogRepository.deleteById(id);
    }

	 public Blog editBlog(String id, BlogDto blogDetails) {
	        Optional<Blog> optionalBlog = blogRepository.findById(id);
	        if (optionalBlog.isPresent()) {
	            Blog blog = optionalBlog.get();
	            blog.setTitle(blogDetails.getTitle());
	            blog.setContent(blogDetails.getContent());
	            return blogRepository.save(blog);
	        } else {
	            throw new RuntimeException("Blog not found with id " + id);
	        }
	    }
	 public Blog updateBlog(String id, String title, String content, MultipartFile photo) throws IOException {
		 
	        Optional<Blog> optionalBlog = blogRepository.findById(id);
	        if (optionalBlog.isPresent()) {
	            Blog blog = optionalBlog.get();
	            blog.setTitle(title);
	            blog.setContent(content);
	            if (photo != null && !photo.isEmpty()) {
	        		String fileName = pictureUploadService.uploadProfilePicture(photo);
		            blog.setPhotoUrl(fileName);
		        }
	            return blogRepository.save(blog);
	        } else {
	            throw new RuntimeException("Blog not found with id " + id);
	        }
	    }
}
