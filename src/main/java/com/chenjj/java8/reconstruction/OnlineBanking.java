package com.chenjj.java8.reconstruction;

public abstract class OnlineBanking {
    public void processCustomer(int id) {
        Customer customer = Database.getCustomerWithId(id);
        makeCustomerHappy(customer);
    }

    abstract void makeCustomerHappy(Customer c);

    static private class Customer {
    }

    static private class Database {
        static Customer getCustomerWithId(int id) {
            return new Customer();
        }
    }
}
