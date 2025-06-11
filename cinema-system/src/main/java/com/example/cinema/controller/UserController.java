package com.example.cinema.controller;

   import com.example.cinema.dto.UserRegistrationRequest;
   import com.example.cinema.entity.User;
   import com.example.cinema.service.UserService;
   import org.springframework.http.ResponseEntity;
   import org.springframework.web.bind.annotation.*;

   import java.util.Map;

   @RestController
   @RequestMapping("/api/users")
   public class UserController {
       private final UserService userService;

       public UserController(UserService userService) {
           this.userService = userService;
       }

       @PostMapping("/register")
       public ResponseEntity<?> register(@RequestBody UserRegistrationRequest request) {
           try {
               userService.register(request);
               return ResponseEntity.ok(Map.of("message", "Vui lòng nhập mã xác nhận được gửi đến email"));
           } catch (Exception e) {
               return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
           }
       }

       @PostMapping("/verify")
       public ResponseEntity<?> verify(@RequestBody Map<String, String> request) {
           try {
               userService.verifyUser(request.get("email"), request.get("code"));
               return ResponseEntity.ok(Map.of("message", "Xác nhận thành công! Bạn có thể đăng nhập."));
           } catch (Exception e) {
               return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
           }
       }

       @GetMapping("/{username}")
       public ResponseEntity<User> getUser(@PathVariable String username) {
           return userService.findByUsername(username)
               .map(ResponseEntity::ok)
               .orElse(ResponseEntity.notFound().build());
       }
   }