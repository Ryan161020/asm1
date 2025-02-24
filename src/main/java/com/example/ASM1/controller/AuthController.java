package com.example.ASM1.controller;

import com.example.ASM1.Entity.User;
import com.example.ASM1.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());
        return "dangnhap";
    }
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "dangky";
    }
    @PostMapping("/create-account")
    public String createAccount(@ModelAttribute User user,
                                @RequestParam(value = "confirmPassword", required = false) String confirmPassword,
                                Model model) {
        if (confirmPassword != null && !user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Mật khẩu xác nhận không khớp");
            model.addAttribute("user", user);
            return "dangky";
        }
        if (userRepository.findByEmail(user.getEmail()).orElse(null) != null) {
            model.addAttribute("error", "Email đã tồn tại");
            model.addAttribute("user", user);
            return "dangky";
        }
        String rawPassword = user.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("🔑 Mật khẩu gốc: " + rawPassword);
        System.out.println("🔐 Mật khẩu mã hóa: " + encodedPassword);

        user.setPassword(encodedPassword);
        user.setRole("ROLE_USER");
        userRepository.save(user);
        System.out.println(user);
        return "redirect:/auth/login";
    }
    @GetMapping("/test-password")
    @ResponseBody
    public String testPassword(@RequestParam String email, @RequestParam String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            boolean isPasswordMatch = passwordEncoder.matches(password, user.getPassword());
            return "Mật khẩu khớp: " + isPasswordMatch;
        }
        return "Không tìm thấy người dùng!";
    }
//    @GetMapping("/logout")
//    public String logout(HttpServletRequest request, HttpServletResponse response) {
//        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
//        return "redirect:/auth/login?logout=true";
//    }

}
