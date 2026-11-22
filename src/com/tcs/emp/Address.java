package com.tcs.emp;

/*//for shallow cloning
public class Address { //Level-2
   */

//for deep cloning
public class Address implements Cloneable {

    String line1;
    String line2;
    String city;

    public Address(String _line1,String _line2, String _city) {
        super();
        this.line1=_line1;
        this.line2=_line2;
        this.city=_city;
    }

    //deep-cloning

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
