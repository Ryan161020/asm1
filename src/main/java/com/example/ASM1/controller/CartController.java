package com.example.ASM1.controller;

import com.example.ASM1.Entity.CartItem;
import com.example.ASM1.Entity.Order;
import com.example.ASM1.Entity.OrderItem;
import com.example.ASM1.Entity.Product;
import com.example.ASM1.service.CartService;
import com.example.ASM1.service.OrderService;
import com.example.ASM1.service.OrderServiceImpl;
import com.example.ASM1.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderServiceImpl orderServiceImpl;
    @Autowired
    private CartService cartService;


    // ✅ Hàm lấy giỏ hàng từ session
    @ModelAttribute("cart")
    public List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);  // Cập nhật vào session ngay lập tức
        }
        return cart;
    }


    // ✅ API Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam("productId") Integer productId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        if (productId == null) {
            response.put("status", "error");
            response.put("message", "Không tìm thấy sản phẩm!");
            return ResponseEntity.badRequest().body(response);
        }

        List<CartItem> cart = getCart(session);
        System.out.println(cart.size());
        Optional<Product> optProduct = productService.findById(productId);

        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            boolean found = false;

            for (CartItem item : cart) {
                // ✅ Cách sửa lỗi
                if (item.getProduct().getProductId() == product.getProductId()) { // Dùng == thay vì .equals()
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }

            if (!found) {
                cart.add(new CartItem(product, 1));
            }

            session.setAttribute("cart", cart);

            response.put("status", "success");
            response.put("message", "Sản phẩm đã được thêm vào giỏ hàng!");
            response.put("cartSize", cart.size());
            return ResponseEntity.ok(response);
        } else {
            response.put("status", "error");
            response.put("message", "Sản phẩm không tồn tại!");
            return ResponseEntity.badRequest().body(response);
        }
    }





    // ✅ API Kiểm tra giỏ hàng (Debug)
    @GetMapping("/debug")
    @ResponseBody
    public Map<String, Object> debugSession(HttpSession session) {
        List<CartItem> cart = getCart(session);
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        response.put("message", cart.isEmpty() ? "Giỏ hàng trống." : "✅ Giỏ hàng có dữ liệu.");
        response.put("cart", cart);
        response.put("status", cart.isEmpty() ? "empty" : "ok");
        return response;
    }

    // ✅ Hiển thị giỏ hàng (Thymeleaf View)
    @GetMapping("/view")
    public String viewCart(HttpSession session, Model model) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
        } else {
            // 🔹 Kiểm tra sản phẩm có null không
            cart.removeIf(item -> item.getProduct() == null);
        }

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart) {
            System.out.println(item.getProduct().getProductId());
            totalPrice = totalPrice.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        System.out.println(totalPrice);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotalPrice = currencyFormatter.format(totalPrice);
        System.out.println(totalPrice);
        model.addAttribute("totalPrice", formattedTotalPrice);
        model.addAttribute("cart", cart);
        return "giohang";
    }



    // ✅ Xóa sản phẩm khỏi giỏ hàng
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam("productId") int productId,
                                                              HttpSession session) {
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getProductId() == productId);
        session.setAttribute("cart", cart);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Sản phẩm đã được xóa khỏi giỏ hàng!");
        response.put("cartSize", cart.size());

        return ResponseEntity.ok(response);
    }

    // ✅ Xóa toàn bộ giỏ hàng
    @PostMapping("/clear")
    public String clearCart(HttpSession session, SessionStatus sessionStatus) {
        session.removeAttribute("cart");
        sessionStatus.setComplete();
        return "redirect:/cart/view";
    }

    // ✅ Tăng số lượng sản phẩm
    @PostMapping("/increase")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> increaseQuantity(@RequestParam("productId") int productId,
                                                                HttpSession session) {
        List<CartItem> cart = getCart(session);
        for (CartItem item : cart) {
            if (item.getProduct().getProductId() == productId) {
                item.setQuantity(item.getQuantity() + 1);
                break;
            }
        }
        session.setAttribute("cart", cart);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Số lượng sản phẩm đã tăng!");
        return ResponseEntity.ok(response);
    }

    // ✅ Giảm số lượng sản phẩm
    @PostMapping("/decrease")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> decreaseQuantity(@RequestParam("productId") int productId,
                                                                HttpSession session) {
        List<CartItem> cart = getCart(session);
        Iterator<CartItem> iterator = cart.iterator();
        while (iterator.hasNext()) {
            CartItem item = iterator.next();
            if (item.getProduct().getProductId() == productId) {
                if (item.getQuantity() > 1) {
                    item.setQuantity(item.getQuantity() - 1);
                } else {
                    iterator.remove(); // Xóa sản phẩm nếu số lượng = 0
                }
                break;
            }
        }
        session.setAttribute("cart", cart);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Số lượng sản phẩm đã giảm!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout")
    public String checkoutPage(Model model, HttpSession session) {
        List<CartItem> cart = cartService.getCart(session);
        BigDecimal totalPrice = cartService.calculateTotal(cart);

        model.addAttribute("cart", cart);
        model.addAttribute("totalPrice", totalPrice);
        return "checkout-form"; // Trả về trang checkout.html
    }

    // ✅ Xử lý thanh toán
    @PostMapping("/checkout")
    public String processCheckout(
            @RequestParam String customerName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String shippingAddress,
            @RequestParam String paymentMethod,
            HttpSession session, Model model) {

        List<CartItem> cart = cartService.getCart(session);
        if (cart.isEmpty()) {
            return "redirect:/cart/view"; // Nếu giỏ hàng trống, quay lại trang giỏ hàng
        }

        // ✅ Tạo đơn hàng mới
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString().substring(0, 10)); // Tạo số đơn hàng ngẫu nhiên
        order.setCustomerName(customerName);
        order.setEmail(email);
        order.setPhone(phone);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(cartService.calculateTotal(cart));

        // ✅ Thêm sản phẩm vào đơn hàng
        for (CartItem item : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductName(item.getProduct().getName());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(orderItem);
            order.setOrderDate(new Date());
        }

        orderService.saveOrder(order); // ✅ Lưu đơn hàng vào DB
        cartService.clearCart(session); // ✅ Xóa giỏ hàng sau khi đặt hàng thành công

        model.addAttribute("order", order);
        return "chiTietDonHang"; // Chuyển hướng đến trang thông báo đặt hàng thành công
    }
    @GetMapping("/order/details/{id}")
    public String orderDetails(@PathVariable Long id, Model model) {
        Order order = orderServiceImpl.findOrderById(id);
        if(order == null){
            // Xử lý khi đơn hàng không tồn tại (redirect hoặc thông báo lỗi)
            return "redirect:/order/error";
        }
        model.addAttribute("order", order);
        return "chiTietDonHang";
    }


}
