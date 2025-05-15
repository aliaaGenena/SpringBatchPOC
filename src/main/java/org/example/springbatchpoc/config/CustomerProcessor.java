package org.example.springbatchpoc.config;

import org.example.springbatchpoc.entity.Customer;
import org.springframework.batch.item.ItemProcessor;


public class CustomerProcessor implements ItemProcessor<Customer, Customer> {

	@Override
	public Customer process(Customer item) throws Exception {

		// logic
		if (item.getEmail().contentEquals("cechaliedl@com.com")) {

			// sleep the main thread for 1 min
	//		Thread.sleep(10000);
			System.exit(1); // simulate crash

		}

		return item;
	}

}
