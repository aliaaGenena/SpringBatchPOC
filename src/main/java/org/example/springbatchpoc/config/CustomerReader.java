//package org.example.springbatchpoc.config;
//
//import org.example.springbatchpoc.entity.Customer;
//import org.springframework.batch.item.ExecutionContext;
//import org.springframework.batch.item.ItemReader;
//import org.springframework.batch.item.ItemStream;
//import org.springframework.batch.item.file.FlatFileItemReader;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CustomerReader implements ItemReader<Customer>, ItemStream {
//
//    private final FlatFileItemReader<Customer> delegate;
//    private int lineNumber = 0;
//
//    public CustomerReader(@Qualifier("customerFlatFileReader") FlatFileItemReader<Customer> delegate) {
//        this.delegate = delegate;
//    }
//
//    @Override
//    public Customer read() throws Exception {
//        Customer item = delegate.read();
//        if (item != null) {
//            lineNumber++;
//            System.out.println("Reading line: " + lineNumber + " → " + item.getEmail());
//        }
//        return item;
//    }
//
//    @Override
//    public void open(ExecutionContext executionContext) {
//        System.out.println(">>> OPENING CustomerReader with ExecutionContext");
//        delegate.open(executionContext);
//        lineNumber = 0;
//    }
//
//    @Override
//    public void update(ExecutionContext executionContext) {
//        delegate.update(executionContext);
//    }
//
//    @Override
//    public void close() {
//        delegate.close();
//    }
//}