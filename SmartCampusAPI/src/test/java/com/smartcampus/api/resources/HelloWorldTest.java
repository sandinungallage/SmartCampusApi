public class HelloWorldTest {
    @org.junit.Test
    public void testHelloWorld() {
        String expected = "Hello, World!";
        String actual = new HelloWorld().sayHello();
        org.junit.Assert.assertEquals(expected, actual);
    }
}

public class RoomResourceTest {
    @org.junit.Test
    public void testRoomResource() {
        // Add your test logic here
    }
}