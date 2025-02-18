package com.example.ASM1.controller;

import com.example.ASM1.Entity.CartItem;
import com.example.ASM1.Entity.Order;
import com.example.ASM1.DTO.OrderForm;
import com.example.ASM1.Entity.OrderItem;
import com.example.ASM1.Entity.Product;


import com.example.ASM1.service.OrderService;
import com.example.ASM1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;


@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    // Khởi tạo giỏ hàng trong session nếu chưa có
    @ModelAttribute("cart")
    public List<CartItem> initializeCart() {
        return new ArrayList<>();
    }

    // Hiển thị trang giỏ hàng (view: giohang.html)
    @GetMapping("/view")
    public String viewCart(@ModelAttribute("cart") List<CartItem> cart, Model model) {
        // Tính tổng tiền: mỗi mục = product.price * quantity
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart) {
            totalPrice = totalPrice.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        // Định dạng tổng tiền theo định dạng tiền tệ của Việt Nam
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotalPrice = currencyFormatter.format(totalPrice);

        model.addAttribute("totalPrice", formattedTotalPrice);
        model.addAttribute("cart", cart);
        return "giohang"; // tên view (giohang.html)
    }



    // Thêm sản phẩm vào giỏ hàng
    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") int productId,
                            @ModelAttribute("cart") List<CartItem> cart) {

        // Lấy thông tin sản phẩm từ database qua service
        Optional<Product> optProduct = productService.findById(productId);
        if (optProduct.isPresent()) {
            Product product = optProduct.get();
            boolean found = false;
            // Kiểm tra nếu sản phẩm đã có trong giỏ thì tăng số lượng
            for (CartItem item : cart) {
                if (item.getProduct().getProductId() == product.getProductId()) {
                    item.setQuantity(item.getQuantity() + 1);
                    found = true;
                    break;
                }
            }
            // Nếu chưa có, thêm mới sản phẩm vào giỏ với số lượng 1
            if (!found) {
                cart.add(new CartItem(product, 1));
            }
        }
        return "redirect:/product/findAll";
    }

    // Xóa một sản phẩm khỏi giỏ hàng theo productId
    @PostMapping("/remove")
    public String removeFromCart(@RequestParam("productId") int productId,
                                 @ModelAttribute("cart") List<CartItem> cart) {
        cart.removeIf(item -> item.getProduct().getProductId() == productId);
        return "redirect:/cart/view";
    }

    // Xóa toàn bộ giỏ hàng
    @PostMapping("/clear")
    public String clearCart(SessionStatus sessionStatus) {
        sessionStatus.setComplete(); // Xóa attribute "cart" khỏi session
        return "redirect:/cart/view";
    }

    // Tăng số lượng sản phẩm
    @PostMapping("/increase")
    public String increaseQuantity(@RequestParam("productId") int productId,
                                   @ModelAttribute("cart") List<CartItem> cart) {
        for (CartItem item : cart) {
            // Giả sử entity Product có getter là getId()
            if (item.getProduct().getProductId()     == productId) {
                item.setQuantity(item.getQuantity() + 1);
                break;
            }
        }
        return "redirect:/cart/view";
    }

    // Giảm số lượng sản phẩm
    @PostMapping("/decrease")
    public String decreaseQuantity(@RequestParam("productId") int productId,
                                   @ModelAttribute("cart") List<CartItem> cart) {
        // Dùng iterator để tránh ConcurrentModificationException
        for (int i = 0; i < cart.size(); i++) {
            CartItem item = cart.get(i);
            if (item.getProduct().getProductId() == productId) {
                int currentQty = item.getQuantity();
                if (currentQty > 1) {
                    item.setQuantity(currentQty - 1);
                } else {
                    // Nếu số lượng chỉ còn 1, bạn có thể lựa chọn xóa sản phẩm khỏi giỏ hàng
                    cart.remove(i);
                }
                break;
            }
        }
        return "redirect:/cart/view";
    }
    // Hiển thị form thanh toán (checkout)
    @GetMapping("/checkout")
    public String checkoutForm(@ModelAttribute("cart") List<CartItem> cart, Model model) {
        if (cart.isEmpty()) {
            return "redirect:/cart/view";
        }
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart) {
            totalPrice = totalPrice.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }



        model.addAttribute("orderForm", new OrderForm());
        return "checkout-form"; // Tên file view: checkout-form.html
    }

    @PostMapping("/checkout")
    public String processCheckout(@ModelAttribute("orderForm") OrderForm orderForm,
                                  @ModelAttribute("cart") List<CartItem> cart,
                                  Model model,
                                  SessionStatus sessionStatus) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem item : cart) {
            totalAmount = totalAmount.add(
                    item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }

        // Định dạng tổng số tiền thành VND
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedTotalAmount = currencyFormatter.format(totalAmount) + " VND";

        Order order = new Order();
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setCustomerName(orderForm.getCustomerName());
        order.setEmail(orderForm.getEmail());
        order.setPhone(orderForm.getPhone());
        order.setShippingAddress(orderForm.getShippingAddress());
        order.setPaymentMethod(orderForm.getPaymentMethod());
        order.setOrderDate(new Date());
        order.setTotalAmount(totalAmount);
        order.setStatus("processing");

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductName(cartItem.getProduct().getName());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getProduct().getPrice());
            orderItem.setTotal(cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setOrder(order);
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        Order savedOrder = orderService.saveOrder(order);

        model.addAttribute("order", savedOrder);
        model.addAttribute("formattedTotalAmount", formattedTotalAmount);

        sessionStatus.setComplete();

        return "chiTietDonHang"; // View hiển thị chi tiết đơn hàng đã lưu
    }

}
