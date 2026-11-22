package com.tcs.emp;

public class Driver {
    static Address address; //Declared here to understand Memory Leak
    static Employee emp1;
    public static void main(String[] args) throws CloneNotSupportedException {

        address = new Address("SLC Royal Co-live","1st C Cross Rd","Bengaluru");
        emp1 = new Employee("Jasraj Barik","JSD878",address);

        Employee clonedEmp = (Employee) emp1.clone();

        emp1.address.city="Hyderabad";

        System.out.println(emp1.address.city);
        System.out.println(clonedEmp.address.city);

        address = null; //Garbage Collector will now remove this object
        emp1 = null;    // Unwanted objects will be removed so the memory available will be increased.
                      // Less chances of OutOfMemoryError i.e. Memory Leak.

    }

}
