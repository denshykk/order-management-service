package com.griddynamics.reactive.course.ordermanagementservice.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import com.griddynamics.reactive.course.ordermanagementservice.domain.User;

@Repository
public interface UserRepository extends ReactiveMongoRepository<User, String> {

}
