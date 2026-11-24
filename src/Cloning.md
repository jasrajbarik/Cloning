# Java Cloning

## 1. What Is Cloning in Java?

Cloning in Java means creating a new object with the same state (field values) as an existing object.

In simple terms, cloning creates a copy of an existing object.

For example, suppose we have an object:

```java
Student s1 = new Student(101, "Jasraj");
```

We can create another object containing the same field values:

```java
Student s2 = (Student) s1.clone();
```

Now:
- `s1` and `s2` refer to two different objects.
- Both objects initially contain the same field values.
- Changing a field in one object does not necessarily change the corresponding field in the other. This depends on whether the copy is shallow or deep.

## 2. The `clone()` Method

Java provides a `clone()` method in the `Object` class.

```java
protected native Object clone() throws CloneNotSupportedException;
```

Important points:

1. `clone()` is defined in the `Object` class.
2. Its return type is `Object`.
3. It is declared `protected`.
4. It throws `CloneNotSupportedException`.
5. It creates and returns a copy of the object.
6. It does not call the class constructor to create the cloned object.

Because the return type is `Object`, we generally need to typecast the result into the required class.

```java
Student s2 = (Student) s1.clone();
```

## 3. The `Cloneable` Interface

`Cloneable` is a marker interface available in the `java.lang` package.

A marker interface does not declare methods. It marks a class to indicate that it supports a particular capability.

To use the default cloning mechanism provided by `Object.clone()`, the class must implement `Cloneable`.

### Why Is `Cloneable` Required?

If a class does not implement `Cloneable` and its `clone()` method is invoked, the default implementation of `Object.clone()` throws `CloneNotSupportedException`.

Example:

```java
class Student {
    int id;
    String name;
}
```

This class does not implement `Cloneable`. Calling its inherited `clone()` method from an accessible context will result in `CloneNotSupportedException`.

Correct approach:

```java
class Student implements Cloneable {
    int id;
    String name;
}
```

**Important:** Implementing `Cloneable` does not itself provide a public `clone()` method. It simply indicates that the default `Object.clone()` mechanism is permitted for that class.

## 4. Creating a Cloneable Class

A common approach is to override `clone()` and make it public.

```java
class Student implements Cloneable {

    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Student s1 = new Student(101, "Jasraj");

        Student s2 = (Student) s1.clone();

        System.out.println(s1.id);
        System.out.println(s1.name);

        System.out.println(s2.id);
        System.out.println(s2.name);

        System.out.println(s1 == s2);
    }
}
```

### Output

```text
101
Jasraj
101
Jasraj
false
```

### Explanation

- `s1` refers to the original object.
- `s2` refers to the cloned object.
- Both objects initially have the same field values.
- `s1 == s2` returns `false` because they are different objects in memory.

The expression:

```java
return super.clone();
```

calls the `clone()` implementation of the parent class, which is `Object`.

## 5. Why Do We Use `super.clone()`?

Consider:

```java
@Override
public Object clone() throws CloneNotSupportedException {
    return super.clone();
}
```

Here, `super.clone()` invokes the cloning mechanism provided by `Object`.

The default `Object.clone()` mechanism:

1. Creates a new object of the same runtime class.
2. Copies the values of the object's fields into the new object.
3. Does not invoke the constructor of the class being cloned.
4. Performs a field-by-field copy, not a recursive copy of referenced objects.

This default copying behavior is called **shallow copying**.

## 6. Shallow Copy

A shallow copy creates a new object, but copies the field values as they are.

For primitive fields, their values are copied.

For reference fields, the reference itself is copied, not the referenced object.

As a result, the original and cloned objects can share the same nested object.

### Example

```java
class Address {

    String city;

    Address(String city) {
        this.city = city;
    }
}

class Student implements Cloneable {

    int id;
    String name;
    Address address;

    Student(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Address address = new Address("Bengaluru");

        Student s1 = new Student(101, "Jasraj", address);

        Student s2 = (Student) s1.clone();

        System.out.println(s1 == s2);

        System.out.println(s1.address == s2.address);

        s2.address.city = "Delhi";

        System.out.println(s1.address.city);
        System.out.println(s2.address.city);
    }
}
```

### Output

```text
false
true
Delhi
Delhi
```

### Explanation

First comparison:

```java
s1 == s2
```

Returns `false` because two different Student objects exist.

Second comparison:

```java
s1.address == s2.address
```

Returns `true` because both Student objects refer to the same Address object.

When we execute:

```java
s2.address.city = "Delhi";
```

The shared Address object is modified. Therefore, both students observe the updated city.

### Shallow Copy Diagram

```text
          s1                         s2
           |                          |
           v                          v
    +-------------+            +-------------+
    |   Student   |            |   Student   |
    +-------------+            +-------------+
    | id = 101    |            | id = 101    |
    | name        |            | name        |
    | address ----|-----+------|---- address |
    +-------------+     |      +-------------+
                        |
                        v
                 +-------------+
                 |   Address   |
                 +-------------+
                 | city        |
                 | Bengaluru   |
                 +-------------+
```

Both Student objects point to the same Address object.

## 7. Deep Copy

A deep copy creates a new object and also creates independent copies of its referenced mutable objects.

The original and cloned objects do not share those copied nested objects.

### Example

```java
class Address implements Cloneable {

    String city;

    Address(String city) {
        this.city = city;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class Student implements Cloneable {

    int id;
    String name;
    Address address;

    Student(int id, String name, Address address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        Student clonedStudent = (Student) super.clone();

        clonedStudent.address = (Address) address.clone();

        return clonedStudent;
    }
}

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Address address = new Address("Bengaluru");

        Student s1 = new Student(101, "Jasraj", address);

        Student s2 = (Student) s1.clone();

        System.out.println(s1 == s2);

        System.out.println(s1.address == s2.address);

        s2.address.city = "Delhi";

        System.out.println(s1.address.city);
        System.out.println(s2.address.city);
    }
}
```

### Output

```text
false
false
Bengaluru
Delhi
```

### Explanation

The following statement creates a shallow copy of the Student object:

```java
Student clonedStudent = (Student) super.clone();
```

Then:

```java
clonedStudent.address = (Address) address.clone();
```

creates a separate Address object and assigns it to the cloned Student.

Now:
- `s1` and `s2` are different Student objects.
- `s1.address` and `s2.address` are different Address objects.
- Changing the cloned student's city does not affect the original student's city.

### Deep Copy Diagram

```text
          s1                         s2
           |                          |
           v                          v
    +-------------+            +-------------+
    |   Student   |            |   Student   |
    +-------------+            +-------------+
    | id = 101    |            | id = 101    |
    | name        |            | name        |
    | address ----|--+      +--|---- address |
    +-------------+  |      |  +-------------+
                     v      v
              +-------------+    +-------------+
              |   Address   |    |   Address   |
              +-------------+    +-------------+
              | Bengaluru   |    | Delhi       |
              +-------------+    +-------------+
```

Each Student has its own Address object.

## 8. Difference Between Shallow Copy and Deep Copy

| Feature                | Shallow Copy          | Deep Copy                               |
|------------------------|-----------------------|-----------------------------------------|
| Main object            | New object            | New object                              |
| Primitive fields       | Values copied         | Values copied                           |
| Reference fields       | References copied     | Referenced objects independently copied |
| Nested mutable objects | Shared                | Not shared when correctly deep-copied   |
| Memory usage           | Usually lower         | Usually higher                          |
| Implementation         | Often `super.clone()` | Requires copying nested objects too     |

**Remember:** `Object.clone()` performs a shallow copy. Deep copying must be implemented separately.

## 9. Cloning and Constructors

One important feature of the default `Object.clone()` mechanism is that it does not invoke the constructor of the class being cloned.

Example:

```java
class Student implements Cloneable {

    int id;

    Student(int id) {
        this.id = id;
        System.out.println("Constructor called");
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Student s1 = new Student(101);

        Student s2 = (Student) s1.clone();
    }
}
```

### Output

```text
Constructor called
```

The constructor runs when `s1` is created. It does not run again when `s2` is created through the default cloning mechanism.

## 10. Cloning and Inheritance

Suppose a child class inherits from a parent class.

If the child class implements `Cloneable`, the default `Object.clone()` mechanism can clone an instance of that child class.

The clone retains the object's runtime class and copies its inherited and declared fields.

Example:

```java
class Parent {

    int x = 10;
}

class Child extends Parent implements Cloneable {

    int y = 20;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Child c1 = new Child();

        Child c2 = (Child) c1.clone();

        System.out.println(c2.x);
        System.out.println(c2.y);
    }
}
```

### Output

```text
10
20
```

The cloned Child object contains copies of both inherited and child-class fields.

## 11. `CloneNotSupportedException`

`CloneNotSupportedException` is a checked exception in Java.

It is thrown by the default `Object.clone()` implementation when the object's class does not implement `Cloneable`.

Example:

```java
class Student {

    int id = 101;

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
```

Calling this method results in `CloneNotSupportedException` because Student does not implement `Cloneable`.

Correct approach:

```java
class Student implements Cloneable {
    // Class implementation
}
```

You must still handle or declare the checked exception when calling a method that declares it.

## 12. Cloning an Array

Arrays in Java support cloning.

The `clone()` method of an array returns a new array of the same runtime type.

Example:

```java
public class Main {

    public static void main(String[] args) {

        int[] arr1 = {10, 20, 30, 40};

        int[] arr2 = arr1.clone();

        arr2[0] = 100;

        System.out.println(arr1[0]);
        System.out.println(arr2[0]);
    }
}
```

### Output

```text
10
100
```

For a primitive array, cloning creates a new array and copies its primitive values.

For an array of objects, the array itself is copied, but its elements' references are copied. Therefore, the original and cloned arrays can still refer to the same element objects.

## 13. Cloning vs Copy Constructor

A copy constructor is a constructor that creates an object using another object of the same class as input.

Example:

```java
class Student {

    int id;
    String name;

    Student(int id, String name) {
        this.id = id;
        this.name = name;
    }

    Student(Student other) {
        this.id = other.id;
        this.name = other.name;
    }
}

public class Main {

    public static void main(String[] args) {

        Student s1 = new Student(101, "Jasraj");

        Student s2 = new Student(s1);
    }
}
```

| Cloning                                           | Copy Constructor                           |
|---------------------------------------------------|--------------------------------------------|
| Uses the `clone()` mechanism                      | Uses a constructor                         |
| Default `Object.clone()` performs shallow copying | Copy behavior is explicitly defined        |
| Requires `Cloneable` for default `Object.clone()` | Does not require `Cloneable`               |
| Does not invoke the class constructor             | Invokes the copy constructor               |
| Can throw `CloneNotSupportedException`            | Does not inherently require this exception |

A copy constructor often gives better control over how an object is copied.

## 14. Cloning vs Serialization

Serialization and deserialization can also be used to create a copy of an object graph, provided the involved objects are serializable and the serialization process is correctly implemented.

| Cloning                                        | Serialization-Based Copying                                      |
|------------------------------------------------|------------------------------------------------------------------|
| Uses the `clone()` mechanism                   | Uses serialization and deserialization                           |
| Default clone is shallow                       | Serialization-based copying can recreate a separate object graph |
| Requires `Cloneable` for default cloning       | Requires serializable objects                                    |
| Usually less overhead                          | Usually more overhead                                            |
| Does not normally invoke the class constructor | Deserialization has its own constructor behavior                 |

Serialization-based copying is not automatically a good replacement for cloning. It can be slower and has additional constraints.

## 15. Important Rules to Remember

1. `clone()` is defined in the `Object` class.
2. `Object.clone()` is protected.
3. `Cloneable` is a marker interface.
4. A class must implement `Cloneable` to use the default `Object.clone()` mechanism successfully.
5. The default `Object.clone()` performs shallow copying.
6. `super.clone()` invokes the parent implementation of `clone()`.
7. The default cloning mechanism does not call the constructor of the cloned class.
8. Primitive field values are copied.
9. Reference field values are copied as references.
10. Deep copying requires additional implementation.
11. `CloneNotSupportedException` is a checked exception.
12. Arrays support cloning.

## 16. Common Interview Questions

### Q1. What is cloning in Java?

Cloning is the process of creating a new object with the same field values as an existing object.

### Q2. Which class provides the `clone()` method?

The `Object` class provides the `clone()` method.

### Q3. Why do we implement the `Cloneable` interface?

It marks a class as eligible for the default cloning mechanism provided by `Object.clone()`.

### Q4. Is `Cloneable` a functional interface?

No. It is a marker interface and does not declare any methods.

### Q5. What happens if a class does not implement `Cloneable`?

The default implementation of `Object.clone()` throws `CloneNotSupportedException`.

### Q6. What is the default type of copying performed by `Object.clone()`?

Shallow copying.

### Q7. Does cloning call the constructor?

No. The default `Object.clone()` mechanism does not invoke the constructor of the cloned class.

### Q8. What is the difference between shallow copy and deep copy?

A shallow copy copies references to nested objects, whereas a deep copy creates independent copies of the nested objects as well.

### Q9. Can we override `clone()` and make it public?

Yes. A class can override the protected `clone()` method and increase its visibility to public.

### Q10. Is `CloneNotSupportedException` checked or unchecked?

It is a checked exception.

### Q11. Can arrays be cloned in Java?

Yes. Arrays support the `clone()` method.

### Q12. Does implementing `Cloneable` automatically make `clone()` public?

No. It only marks the class as eligible for the default cloning mechanism. A class commonly overrides `clone()` and makes it public.

## 17. Quick Revision

| Concept                      | Meaning                                                        |
|------------------------------|----------------------------------------------------------------|
| Cloning                      | Creating a copy of an existing object                          |
| `Object.clone()`             | Method that performs the default field-by-field copy           |
| `Cloneable`                  | Marker interface                                               |
| Shallow Copy                 | Copies references rather than nested objects                   |
| Deep Copy                    | Creates independent copies of nested objects                   |
| `super.clone()`              | Calls the parent implementation of `clone()`                   |
| `CloneNotSupportedException` | Checked exception thrown when default cloning is not supported |
| Copy Constructor             | Constructor that creates an object from another object         |

## 18. Final Summary

Cloning is a mechanism for creating a copy of an existing Java object.

Java provides the `clone()` method through the `Object` class. To use its default cloning mechanism, the class must implement the `Cloneable` marker interface.

The default implementation performs a shallow copy:
- A new main object is created.
- Primitive field values are copied.
- Reference values are copied, so nested objects may be shared.
- The cloned class's constructor is not invoked.

If independent nested objects are required, deep copying must be implemented.

A copy constructor is another approach that often provides more explicit control over object copying.
