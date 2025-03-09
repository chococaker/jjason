# JJason

[license]: https://github.com/chococaker/jjason/blob/master/LICENSE

A small, minimal, and functional JSON de/serialisation project.
 * ~750 lines
 * ~17KB source file

## Building
This project uses the **Maven** build system.
```shell
git clone https://github.com/chococaker/jjason.git
cd JJason
mvn clean install
```

To include in your Maven project:
```xml
<dependency>
  <groupId>net.chococaker</groupId>
  <artifactId>jjason</artifactId>
  <version>1.0</version>
</dependency>
```

## Requirements
Java 8+. JJason has no extra dependencies, so no need to import any.

## Getting Started

JJason deserialisation is very simple. Simply create a `JsonReader` using
`objectReader(CharSequence)` or `arrayReader(CharSequence)`, using the parameter as the desired
JSON input, and call the reader's `read()` method on it.

**Example:**
```json
{
  "gas-in-tank": 60.0,
  "license-plate": "xx xx 0000",
  "miles-driven": 5000
}
```

```java
class Main {
   public static void main(String[] args) {
      String jsonString = ...;
      
      JsonObject json = JsonReader.objectReader(jsonString).read();

      System.out.println(json);
   }
}
```

**Output:**
```json
{"gas-in-tank":60.0,"miles-driven":5000,"license-plate":"xx xx 0000"}
```

## Working with the library
JJason has three basic types that can work together to produce complex data structures.

### JsonElements
A `JsonElement` can be a `JsonObject`; a key-value tree that contains other `JsonElement`s, a
`JsonArray`; a list of `JsonElement`s, or a `JsonPrimitive`; a wrapper for a String or Java
primitive.

To find out which type it is, use Java's `instanceof` operator or call its utility
methods: `isJsonObject`, `isJsonArray` or `isJsonPrimitive`.

All `JsonElement`s will:
 * Have a `toString()` method that produces valid JSON content.
 * Have a `clone()` method which will produce **deep-copied** results.
 * Have a `getAs[JsonObject/JsonArray/JsonPrimitive]()` method to prevent necessity of ugly casting.
 * Be serialisable.

### JsonObjects
A `JsonObject` is a relatively simple class. It is essentially a tree holding key-value pairs
for `JsonElement`s.

**Useful methods:**
```java
JsonElement get(String key)                // retrieve a value using the given key.
boolean set(String key, JsonElement value) // set a value for the object.
Set<String> keySet()                       // retrieve all keys in the JsonObject.
```

### JsonArrays
A `List` of `JsonElement`s.

### JsonPrimitives
A wrapper class for Java primitives, Strings, and numeric "Big" types (BigInteger, BigDecimal). Use
`get()` to retrieve an `Object` that extends one of these types.

You can also get the `Object` as a specific type, using `getAs[type]()` or even just
`getAs(Class)`.

## Implementing Deserialisation
JJason comes with no native deserialisation support, due to the occasional unexpected behaviour of
the Java Reflection API. However, it is very simple to build a custom implementation, similar to
[Gson's](https://github.com/google/gson) `Gson` class.

```java
class JJason {
   private final Map<Class<?>, Function<?, JsonObject>> adaptorMap = new HashMap<>();

   public <T> void addTypeAdaptor(Class<T> clazz, Function<T, JsonObject> adaptor) {
        adaptorMap.put(clazz, adaptor);
   }

   public <T> JsonObject adapt(Class<T> clazz, T t) {
       return adaptorMap.get(clazz).apply(t);
   }
}
```

This is a very basic implementation with less support for things like inheritance, but it's a quick
and easy way to create type adaptors.

The type adaptor itself is also easy to create:
```java
class CarTypeAdaptor implements Function<Car, JsonObject> {
   @Override
   public JsonObject apply(Car car) {
      JsonObject result = new JsonObject();

      result.set("license-plate", new JsonPrimitive(car.getLicensePlate()));
      result.set("gas-in-tank", new JsonPrimitive(car.getGas()));
      /* etc, etc */
      
      return result;
   }
}
```

## Contributing
Before opening a pull request, make sure that your code follows these guidelines:
 * Commit messages are in present tense. (Ex. Change class name)
 * Documentation is written for any new resources created. Less readable or understandable code
   should be clearly commented.
 * For any documentation or non-source files, make sure to add a breakline for every 100 characters
   in a line.
 * Write the code in a space-efficient way to keep the .jar's source filesize down. Do not write
   methods that aren't absolutely necessary.

## Licence
JJason is released under the [Apache 2.0 Licence](https://www.apache.org/licenses/LICENSE-2.0).

## Note
This framework maximises functionality with a Jarfile size under 20KB. However, there's only so
much that can fit into such a small file. For better functionality, use libraries like
[Gson](https://github.com/google/gson) or
[FasterXML/Jackson](https://github.com/FasterXML/jackson).
