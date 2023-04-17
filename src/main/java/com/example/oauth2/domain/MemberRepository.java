package com.example.oauth2.domain;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long>{

	Optional<Member> findByEmail(String email);	
}
