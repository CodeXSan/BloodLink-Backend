package com.project.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.project.model.Blog;

@Repository
public interface BlogRepository extends MongoRepository<Blog, String>{

}
