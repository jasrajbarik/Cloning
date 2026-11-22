package com.tcs.emp;

public class Employee implements Cloneable { //Level-1
    String name; //copy
    String empCode; //copy
    Address address;    //HAS-A     //copy only the reference not the whole object

    //Shallow Clone: Level-1: Only address reference will be copied but not the actual object

    public Employee(String _name,String _empCode, Address _address) {
        super();
        this.name=_name;
        this.address=_address;
        this.empCode=_empCode;
    }

/*    //for shallow
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }*/

    //for deep-cloning
    @Override
    public Object clone() throws CloneNotSupportedException {
        Employee e = (Employee) super.clone();
        e.address = (Address) address.clone();
        return e;
    }

}
