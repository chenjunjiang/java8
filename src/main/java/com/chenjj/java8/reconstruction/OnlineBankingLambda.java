package com.chenjj.java8.reconstruction;

import java.util.function.Consumer;

public class OnlineBankingLambda {
    public void processCustomer(int id, Consumer<Customer> consumer) {
        Customer customer = Database.getCustomerWithId(id);
        consumer.accept(customer);
    }

    static public class Customer {
    }

    static private class Database {
        static Customer getCustomerWithId(int id) {
            return new Customer();
        }
    }
}
