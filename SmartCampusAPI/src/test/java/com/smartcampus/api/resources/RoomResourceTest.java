import com.smartcampus.api.models.Room;
import com.smartcampus.api.resources.RoomResource;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

/**
 * Integration test class for the RoomResource API.
 * 
 * This class extends JerseyTest, which provides a lightweight
 * testing framework for RESTful services built using Jersey.
 * It allows simulation of HTTP requests without deploying
 * the application to a real server.
 */
class RoomResourceTest extends JerseyTest {

    /**
     * Test case for verifying room creation functionality.
     * 
     * Intended to:
     * - Send a POST request to create a new room
     * - Validate the response status (e.g., 201 Created)
     * - Check if the returned room data matches the input
     */
    @Test
    void testRoomCreation() {

        // TODO: 
        // 1. Create a Room object with test data
        // 2. Send POST request to /rooms endpoint
        // 3. Assert response status and returned entity

    }

    /**
     * Test case for retrieving a specific room.
     * 
     * Intended to:
     * - Send a GET request with a valid room ID
     * - Verify the response status (e.g., 200 OK)
     * - Confirm the returned room details are correct
     */
    @Test
    void testGetRoom() {

        // TODO:
        // 1. Ensure a test room exists (create one if needed)
        // 2. Send GET request to /rooms/{id}
        // 3. Assert response status and response body

    }

    /**
     * Test case for updating an existing room.
     * 
     * Intended to:
     * - Send a PUT request with updated room data
     * - Verify the response status (e.g., 200 OK or 204 No Content)
     * - Confirm that the room details were updated correctly
     */
    @Test
    void testUpdateRoom() {

        // TODO:
        // 1. Create or retrieve an existing room
        // 2. Modify room attributes
        // 3. Send PUT request to /rooms/{id}
        // 4. Assert response and updated values

    }

    /**
     * Test case for deleting a room.
     * 
     * Intended to:
     * - Send a DELETE request for a specific room ID
     * - Verify the response status (e.g., 204 No Content)
     * - Ensure the room is no longer retrievable
     */
    @Test
    void testDeleteRoom() {

        // TODO:
        // 1. Create a room to delete
        // 2. Send DELETE request to /rooms/{id}
        // 3. Assert response status
        // 4. Attempt to retrieve the room and expect failure (e.g., 404)

    }
}