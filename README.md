[![Releases](https://img.shields.io/github/v/release/marcellogalhardo/kotlin-fixture?include_prereleases)](https://github.com/marcellogalhardo/kotlin-fixture/releases) [![Build Status](https://travis-ci.org/marcellogalhardo/kotlin-fixture.svg?branch=master)](https://travis-ci.org/marcellogalhardo/kotlin-fixture)

# Kotlin.Fixture

Kotlin.Fixture is an open source library based on the popular .NET library: [AutoFixture](https://github.com/AutoFixture/AutoFixture).

**This project is currently experimental and the API subject to breaking changes without notice.**

## Description

Write maintainable unit tests, faster.

Kotlin.Fixture makes it easier for developers to do Test-Driven Development by automating non-relevant Test Fixture Setup, allowing the Test Developer to focus on the essentials of each test case.

## Overview

Kotlin.Fixture is designed to make Test-Driven Development more productive and unit tests more refactoring-safe. It does so by removing the need for hand-coding anonymous variables as part of a test's Fixture Setup phase. 

When writing unit tests, you typically need to create some objects that represent the initial state of the test. Often, an API will force you to specify much more data than you really care about, so you frequently end up creating objects that have no influence on the test, simply to make the code compile.

Kotlin.Fixture can help by creating such [Anonymous Variables](http://blogs.msdn.com/ploeh/archive/2008/11/17/anonymous-variables.aspx) for you. Here's a simple example:

```kotlin
val fixture = Fixture {
    register {
        MyClass()
    }
}

@Test
fun testExample() {
    // Arrange
    val expectedNumber = fixture.next<Int>()
    val sut = fixture.next<MyClass>()
    
    // Act
    val result = sut.echo(expectedNumber)
    
    // Assert
    assertTrue(expectedNumber == result)
}
```

This example illustrates the basic principle of Kotlin.Fixture: It can create values of virtually any type without the need for you to explicitly define which values should be used. The number expectedNumber is created by a call to `next<T>()` - this will create a random integer value, saving you the effort of explicitly coming up with one.

The example also illustrates how Kotlin.Fixture can be used as a [SUT Factory](http://blog.ploeh.dk/2009/02/13/SUTFactory.aspx) that creates the actual System Under Test (the MyClass instance).

Using Kotlin.Fixture is as easy as referencing the library and creating a new instance of the Fixture class!

## Download

**Step 1.** Add it in your root *build.gradle* at the end of repositories:
```gradle
allprojects {
	repositories {
		maven { url "https://jitpack.io" }
	}
}
```
**Step 2.** Add the dependency
```gradle
dependencies {
	testImplementation 'com.github.marcellogalhardo:kotlin-fixture:{Tag}'
	androidTestImplementation 'com.github.marcellogalhardo:kotlin-fixture:{Tag}'
}
```
(Please replace `{Tag}` with the [latest version numbers](https://github.com/marcellogalhardo/kotlin-fixture/releases): [![](https://jitpack.io/v/marcellogalhardo/kotlin-fixture.svg)](https://jitpack.io/#marcellogalhardo/kotlin-fixture))

That's it!

**Warning:** This library does work outside tests but it was not designed for it. Use it at your own risk.

**Disclaimer:** This library will be migrated to [Maven Central](https://search.maven.org/) as soon as it moves out of experimental status.

## Usage

All examples assume that a Fixture instance has previously been created like this:
```kotlin
val fixture = Fixture()
```

### Basic Types

| Type               | Sample                           | Result                                                |
|--------------------|----------------------------------|-------------------------------------------------------|
| Boolean            | `fixture.next<Boolean>()`        | `Boolean: false`                                      |
| Char               | `fixture.next<Char>()`           | `Char: A`                                             |
| Double             | `fixture.next<Double>()`         | `Double: 400`                                         |
| Float              | `fixture.next<Float>()`          | `Float: 40.5`                                         |
| Int                | `fixture.next<Int>()`            | `Int: 27`                                             |
| Long               | `fixture.next<Long>()`           | `Long: 5`                                             |
| String             | `fixture.next<String>()`         | `String: "f5cdf6b1-a473-410f-95f3-f427f7abb0c7"`      |
| String with Prefix | `fixture.nextString("name")`     | `String: "name-30a35da1-d681-441b-9db3-77ff51728b58"` |

### Complex Class Type

```kotlin
val autoGeneratedClass = fixture.next<ComplexParent>()
```
- Sample result: 
  - ComplexParent:
    - Child: ComplexChild
    - Name: `String: "f70b67ff-05d3-4498-95c9-de74e1aa0c3c"`
    - Number: `Int: 1`

### Autogenerated Interface Types
```kotlin
val autoGeneratedInterface = fixture.next<MyInterface>()
```
- Sample result: Fixture will return a Proxy object that implements the interface; all method call returns will be generated randomly.

### Autogenerated Object Types
```kotlin
val autoGeneratedInterface = fixture.next<MyObject>()
```
- Sample result: Fixture will return an instance of the object; if the object already exists, it will return the same instance.

### Autogenerated List of Strings
```kotlin
var strings = fixture.nextListOf<String>()
```
- Sample result: 
  - `List<String>`
    - `String: "ecc1cc75-cd7a-417f-b477-2913802440b4"`
    - `String: "fce70a7b-fae5-474f-8055-415ca46eac20"`
    - `String: "79b45532-d66f-4abc-9311-77ba68dc9e3c"`
    
### Set Property (Data Classes)

Kotlin.Fixture does not support [Test Data Builder](http://www.natpryce.com/articles/000714.html) pattern.
However, Kotlin's Data Class allows you to easily simulate the pattern:
```kotlin
var person = fixture.next<Person>().copy(name = "Marcello Galhardo")
```
- Sample result: 
  - Person:
    - Name: `Marcello Galhardo`
    - Age: `Int: 25`

### Replaced Default Algorithm

```kotlin
fixture.register<String> { "Marcello Galhardo" }
val result = fixture.next<String>()
```
- Sample result: `String: "Marcello Galhardo"`


#### Abstract Types

```kotlin
fixture.register<MyClass> {
    FakeClass()
}
```
- Sample result: Every time the fixture instance is asked to create an instance of MyClass, it will return a new instance of FakeClass.


## Known Issues

These are the list of well-known issues with Kotlin.Fixture.

- Abstract Classes: if not registered a custom abstract type, Kotlin.Fixture will throw "AbstractClassNotSupportedException"

- Nullability: nullability is ignored and Kotlin.Fixture will always return a non-null instance.
