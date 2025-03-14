package holyhack.eldermind.DatabaseHandling;

import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class FeedReaderDbHelper {

    private static final String TAG = "FeedReaderDbHelper";
    private static final String BASE_URL = "https://0e55-212-123-8-171.ngrok-free.app/api.php";
    private RequestQueue requestQueue;

    // ✅ Define callback interfaces (NO STATIC)
    public interface VolleyCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public interface JsonCallback {
        void onSuccess(JSONArray response);
        void onError(String error);
    }

    // ✅ Constructor initializes Volley request queue
    public FeedReaderDbHelper(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // ✅ INSERT User via API
    public void insertUserData(String username, String email, String password, String date, String country, final VolleyCallback callback) {
        String query = "INSERT INTO users (username,email,passwordCreate,dateCreate,countryCreate) VALUES ('" +
                username + "','" + email + "','" + password + "','" + date + "','" + country + "')";

        sendPostRequest(query, callback);
    }

    // ✅ UPDATE User via API
    public void updateUserData(String username, String email, String password, String date, String country, final VolleyCallback callback) {
        String query = "UPDATE users SET email='" + email + "', passwordCreate='" + password + "', dateCreate='" +
                date + "', countryCreate='" + country + "' WHERE username='" + username + "'";

        sendPostRequest(query, callback);
    }

    // ✅ GET ALL USERS
    public void getAllUsers(final JsonCallback callback) {
        String query = "SELECT * FROM users";
        sendGetRequest(query, callback);
    }

    // ✅ GET USER BY EMAIL
    public void getUserByEmail(String email, final JsonCallback callback) {
        String query = "SELECT * FROM users WHERE email='" + email + "'";
        sendGetRequest(query, callback);
    }

    // ✅ CHECK USER LOGIN
    public void checkUser(String email, String password, final VolleyCallback callback) {
        String query = "SELECT passwordCreate FROM users WHERE email='" + email + "'";
        sendGetRequest(query, new JsonCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    if (response.length() > 0) {
                        JSONObject user = response.getJSONObject(0);
                        String storedPassword = user.getString("passwordCreate");
                        if (storedPassword.equals(password)) {
                            callback.onSuccess("Login Successful");
                        } else {
                            callback.onError("Incorrect password");
                        }
                    } else {
                        callback.onError("User not found");
                    }
                } catch (JSONException e) {
                    callback.onError("Error parsing JSON");
                }
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }

    // ✅ DELETE User by Username
    public void deleteUser(String username, final VolleyCallback callback) {
        String query = "DELETE FROM users WHERE username='" + username + "'";
        sendPostRequest(query, callback);
    }

    // ✅ Generic GET Request (for SELECT queries)
    private void sendGetRequest(String query, final JsonCallback callback) {
        String url = BASE_URL + "?query=" + query.replace(" ", "%20"); // URL encode spaces

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Volley Error: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);
    }

    // ✅ Generic POST Request (for INSERT, UPDATE, DELETE queries)
    private void sendPostRequest(String query, final VolleyCallback callback) {
        String url = BASE_URL;

        Map<String, String> params = new HashMap<>();
        params.put("query", query);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess("Query executed successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError("Volley Error: " + error.getMessage());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
}
}