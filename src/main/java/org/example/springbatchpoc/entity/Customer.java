package org.example.springbatchpoc.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CUSTOMERS_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long internalId;

	@Column(name = "CUSTOMER_ID")
	private int customerId;
	@Column(name = "FIRST_NAME")
	private String firstName;
	@Column(name = "LAST_NAME")
	private String lastName;
	@Column(name = "EMAIL")
	private String email;
	@Column(name = "GENDER")
	private String gender;
	@Column(name = "CONTACT")
	private String contactNo;
	@Column(name = "COUNTRY")
	private String country;
	@Column(name = "DOB")
	private String dob;

}