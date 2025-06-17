package com.example.cinema.service;

import com.example.cinema.dto.UserRegistrationRequest;
import com.example.cinema.entity.User;
import com.example.cinema.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String VERIFICATION_KEY_PREFIX = "verify:";
    private static final int CODE_LENGTH = 6;
    private static final long CODE_TTL_MINUTES = 10;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]\\d{1,14}$");

    @Autowired
    public UserService(UserRepository userRepository, 
                       PasswordEncoder passwordEncoder, 
                       JavaMailSender mailSender, 
                       RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.redisTemplate = redisTemplate;
    }

    public User register(UserRegistrationRequest request) {
        // Validation thủ công
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username không được để trống");
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new IllegalArgumentException("Password phải có ít nhất 6 ký tự");
        }
        if (request.getEmail() == null || !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        if (request.getPhone() != null && !request.getPhone().isEmpty() && 
            !PHONE_PATTERN.matcher(request.getPhone()).matches()) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        String code = generateVerificationCode();
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAvatar(request.getAvatar());
        user.setEnabled(false); // Chưa kích hoạt
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setRole("CUSTOMER");
        user.setVerified(false);
        userRepository.save(user);

        sendVerificationEmail(request.getEmail(), code);
        redisTemplate.opsForValue().set(
            VERIFICATION_KEY_PREFIX + request.getEmail(),
            code,
            CODE_TTL_MINUTES,
            TimeUnit.MINUTES
        );

        return user;
    }

    public void verifyUser(String email, String code) {
        if (email == null || code == null) {
            throw new IllegalArgumentException("Email hoặc mã xác nhận không được để trống");
        }
        String storedCode = redisTemplate.opsForValue().get(VERIFICATION_KEY_PREFIX + email);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new IllegalArgumentException("Mã xác nhận không đúng hoặc đã hết hạn");
        }

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Email không tồn tại"));
        user.setVerified(true);
        user.setEnabled(true);
        userRepository.save(user);
        redisTemplate.delete(VERIFICATION_KEY_PREFIX + email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Loading user: " + username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                System.out.println("User not found: " + username);
                return new UsernameNotFoundException("User not found with username: " + username);
            });
        System.out.println("User found: " + user.getUsername());
        return user;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    private void sendVerificationEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dung20012003@gmail.com");
        message.setTo(email);
        message.setSubject("Xác nhận đăng ký tài khoản Cinema");
        message.setText(
            "Chào bạn,\n\n" +
            "Mã xác nhận của bạn là: " + code + "\n" +
            "Vui lòng nhập mã này trong vòng 10 phút.\n\n" +
            "Trân trọng,\nCinema Team"
        );
        mailSender.send(message);
    }
}