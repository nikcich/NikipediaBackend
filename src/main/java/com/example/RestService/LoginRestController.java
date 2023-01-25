package com.example.RestService;

import com.example.DataCache.RestServiceDataCache;
import com.example.Utility.JsonUtils;
import com.example.Utility.PasswordUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@CrossOrigin(origins = "http://localhost:3000", maxAge = 3600)
@RestController
public class LoginRestController {
    @Autowired
    private RestServiceDataCache cache;

    @RequestMapping("/test")
    public String greeting() {

        return  cache.getCollection("users");
    }

    @PostMapping("/login") // Create New Account
    public ResponseEntity<Map<String, String>> myMethod(@RequestBody HashMap<String, String> request){
        Map<String, String> response = new HashMap<>();
        boolean LogInRequest = request.get("type").equals("0");

        if(request.get("username") == null || request.get("username").length() < 5){
            response.put("errorMessage", "Error: Username Invalid");
            response.put("error", "true");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        if(request.get("email") == null && !LogInRequest){
            response.put("errorMessage", "Error: Email Invalid");
            response.put("error", "true");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }else{
            String emailAddress = request.get("email");
            String regexPattern = "^(.+)@(\\S+)$";
            boolean isValidEmail = Pattern.compile(regexPattern)
                    .matcher(emailAddress)
                    .matches();

            if(!isValidEmail && !LogInRequest){
                response.put("errorMessage", "Error: Email Invalid");
                response.put("error", "true");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        boolean usernameExists = cache.entryExistsWithUsername("users", request.get("username"));

        if(LogInRequest){
            // Handle Login Stuff
            if(!usernameExists){
                response.put("errorMessage", "Error: Username Doesn't Exists");
                response.put("error", "true");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            Document userEntry = cache.findEntryByUsername("users", request.get("username"));
            String pw = userEntry.getString("password");

            boolean pwMatch = PasswordUtils.checkPassword(request.get("password"), pw);

            System.out.println(pwMatch);
            if(!pwMatch){
                response.put("errorMessage", "Error: Invalid Credentials");
                response.put("error", "true");

                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            // Generate unique Hash to represent that the user is logged in

            String cookie = PasswordUtils.hashPassword(request.get("username")+pw);

            // Store the user with the given hash in the cookieUsers map
            System.out.println("Cookies Cache Size: " + cache.userCookies.size());
            System.out.println("Adding cookie: " + cookie);
            cache.cookiePut(cookie, userEntry);

            response.put("cookie", cookie);

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        // Handle Sign Up

        if(usernameExists){
            response.put("errorMessage", "Error: Username Already Exists");
            response.put("error", "true");

            return new ResponseEntity<>(response, HttpStatus.OK);
        }

        String hashedPassword = PasswordUtils.hashPassword(request.get("password"));
        request.put("password", hashedPassword);
        request.remove("confirmPassword");
        request.remove("type");
        String converted = JsonUtils.mapToJson(request);
        System.out.println(converted);

        cache.addToCollection("users", converted);

        response.put("errorMessage", "Error: Unable to login");
        response.put("error", "true");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
