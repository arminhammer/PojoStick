#PojoStick

PojoStick is a small single-file object store for Java programs.  It serializes plain old Java objects into JSON
and stores them in a text file.  PojoStick uses reflection to save type information, so a singlePojoStick store 
can save many different types of objects.


**NOTE:** PojoStick is still in an alpha development stage and should not be used at this time in production!

##Initialization

To use PojoStick, you must initialize a PojoStick.  The constructor takes a single paramete, which is a String
of the pathname of the file.

    import org.arminhammer.pojostick.PojoStick;
    ...
    PojoStick pj = new PojoStick("/home/arminhammer/data/test.pojo");

When the PojoStick is created, it checks to see if the file exists already.  If it exists, it tries to verify that
the file is a valid PojoStick file.  If it does not exist, it attempts to create the file.  If the folder does not
exist, it tries to create it as well.

##Saving Objects

Once a PojoStick has been created, you can start adding objects to it.  Objects are added using the add() method:

    String test1 = "Test String.";
    pj.add(test1);

PojoStick does not require objects to have special annotations or implement an interface like Serializable.  It does,
however, assume that saved objects have a default zero-parameter constructor and implement equals() and hashCode().
These are required to tell objects apart.  PojoStick does not allow duplicate objects to be saved, so equals()
and hashCode() should be able to differentiate objects correctly.

##Retrieving objects
PojoStick provides several methods to retrieve objects.  The first, and most simple, is find():
    
    List<Object> contents = pj.find();

find() retrieves every object in the store and returns them as a List<Object>.  If you want a more limited retrieval,
you can pass a String to find:

    List<Object> testContents = pj.find("Test");

find(String query) returns any object that contains 'query' in one of its fields.

If you want to limit the retrieval by the object type, you can use findType():

    List<String> stringContents = pj.findType(String.class, "Test");

This limits the objects returned to the class in the first parameter.  Instead of returning List<Object>, it returns
a List of the desired class.  In this example, a List<String> is returned.  The second parameter is the query that
is searched in the object fields.  If you only want to search for an object type and don't want to do a text search,
set this to null.

If you want to retrieve a specific object, use find(Object):

    String test = pj.find(test1);

##Updating

PojoStick allows you to update an object in the store with a newer version with the method update():

    String test2 = "Test String 2";
    pj.update(test1, test2);

PojoStick will search for the first object, and replace it with the second object.

##Deleting

PojoStick allows you to delete objects that are in the store with delete():

    pj.delete(test2);

## License
PojoStick is licensed under Apache License 2.0.
