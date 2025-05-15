package org.example.springbatchpoc.repo;

import java.io.Serializable;

import org.example.springbatchpoc.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;



public interface CustomerRepository extends JpaRepository<Customer, Serializable> {

}
