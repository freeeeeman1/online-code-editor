# Code style:

### 0. Comments
No comments except interface contracts and third-party library workarounds (very rare cases). All code stack trace or debug messages should be in english

#### 1. Braces
Braces are always used with ```if, else, for, do``` and ```while``` statements, even when the body is empty or contains only a single statement

Examples:
``` java
if (isValid()) // not accepted
  doSomething();

if (isValid()) { // OK
  doSomething();
}
```

Inline opening brace with ```if``` statement
```java
if (isValid()) // not accepted
{
  doSomething();
}

if (isValid()) { // accepted
  doSomething();
}
```

#### 2. One variable per declaration
Every variable declaration (field or local) declares only one variable: declarations such as 
```java
int a, b;
```
are not used.

#### 3. Declared when needed
Local variables are not habitually declared at the start of their containing block or block-like construct. Instead, local variables are declared close to the point they are first used (within reason), to minimize their scope. Local variable declarations typically have initializers, or are initialized immediately after declaration.

Example:
```java
Set<String> set1 =... //not acceptable, move right before loop

... // some lines of code

Set<String> set2 =... // OK
for(String string: set1) {
  for(String string: set2) {
     ...
  }
}
```

#### 4. Naming

Dont use single letter variables, no underscores in variable name. Try not to shortcut word: 
```java
int p; // bad
int pswd; // bad
int password; // OK
```

Methods should be named as verb:
```java
void validation() { // bad
}

void validate() { // OK
}
```

Try to use ```is```,  ```should``` prefix when method returns boolean value
```java
boolean isOdd(){
}

boolean shouldPass() {
}
```

No ```And``` or ```Or``` conjunctions in method, class, variable and field names
```java
boolean isOddAndDividesByThree() { // bad
}

boolean isEqualsOrGreater() { // bad
}
```



#### 5. Field
Always declare field as final if possible.

#### 6. Source file and class structure

##### 6.1 Imports
Wildcard imports are not used. Use static imports:

```java
import static org.junit.Assert.assertEquals; //accepted
import org.junit.*; //not accepted

Assert.assertEquals(); // not accepted
assertEquals(); //accepted
```

##### 6.2 Class structure
Class method ordering:
```java
class SomeClass {
    public static fields
    private static fields
    public fields
    private fields
    
    private Constructors() {
    }
    
    public Constructors() {
    }
    
    public methods() {
    }
    
    private methods() {
    }
    
    @Override
    equals();
    @Override
    hashCode();
    
    inner classes{
    }
}
```
##### 6.3 Field initialization
Initialize all fields (except static) in constructor:

```java
class SomeClass {
    private A a = new A(); //bad
    
    SomeClass() { // OK
        this.a = new A();
    }
    
    SomeClass(A a) { // OK
        this.a = a;
    }
}
```

Use ```this.field = ...``` for each field initialization

#### 7. Method return values

Do not return boxing types types if not needed:
```java
Boolean isSomething() { // check if boxing is really need
}

boolean isSomething() { //OK
}
```

Do not return error codes, throw exception instead
```java
int doSomething() {
    return -1; // bad
}

void doSomething() throws CheckedException { // OK
    throw new CheckedException();
}
```
#### 8. If statement
##### 8.1 Comparison
Avoid weird boolean comparison: 
```java
if(user.isActive() == false) //bad

if(!user.isActive()) //OK
```

Compare string using equals not "=="
```java
if(username == "Mark") // bad

if(username.equals("Mark") // OK
```
##### 8.2 If structure
Reduce ```If``` tree to ```switch``` statement
```java
// bad
if(a) { 
} else if(b) {
} else if(c) {
}

// bad
if(a) { 
}
if(b) {
}  
if(c) { 
}
```

```java
// OK
switch() {
    case a:
        break;
    case b:
        break;
    case c:
        break;
    default:
}
```

Reduce ```if``` statement with boolean return value
```java
//bad
if(matches()) {
    return true;
} else {
    return false;
}

//OK
return matches();
```

#### 9. Equals and Hashcode
Always override both ```equals()``` and ```hashcode()``` at once if you override one of them. Use IDEA ```ALT+INSERT``` to override them. Always override ```hashCode()``` and ```equals()``` when using ```Class``` objects as key in ```HashMap<Class,Object>```



#### 10. pom.xml structure
Test dependencies should be located lower than compile|runtime:

```xml
<!-- bad. place this after javax.mail -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <scope>test</scope>
</dependency>

 <dependency>
    <groupId>javax.mail</groupId>
    <artifactId>mail</artifactId>
</dependency>

```