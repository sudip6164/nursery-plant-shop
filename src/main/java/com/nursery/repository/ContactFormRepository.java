package com.nursery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nursery.model.ContactForm;

@Repository
public interface ContactFormRepository extends JpaRepository<ContactForm, Integer> {

}
