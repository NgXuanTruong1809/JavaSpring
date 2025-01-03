package vn.hoidanit.laptopshop.controller.admin;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.service.OrderService;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/order")
    public String getOrderPage(Model model, @RequestParam(value = "page", required = false) String page) {
        int pageInt = 1;
        try {
            pageInt = Integer.parseInt(page);
        } catch (Exception exception) {

        }
        Pageable pageable = PageRequest.of(pageInt - 1, 5); // page index start = 0, limit
        Page<Order> orders = this.orderService.getAllOrder(pageable);
        List<Order> listOrders = orders.getContent();
        model.addAttribute("orders", listOrders);
        model.addAttribute("curPage", pageInt);
        model.addAttribute("totalPages", orders.getTotalPages());
        return "admin/order/show";
    }

    @GetMapping("/admin/order/{id}")
    public String getOrderDetailPage(Model model, @PathVariable long id) {
        List<OrderDetail> orderDetails = this.orderService.getOrderDetailList(id);
        model.addAttribute("orderDetails", orderDetails);
        return "admin/order/detail";
    }

    @GetMapping("/admin/order/update/{id}")
    public String getUserDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        Order order = this.orderService.getAOrder(id);
        model.addAttribute("newOrder", order);
        return "admin/order/update";
    }

    @PostMapping("/admin/order/update")
    public String postOrderUpdate(@ModelAttribute("newOrder") Order order) {

        Order orderOld = this.orderService.getAOrder(order.getId());
        orderOld.setStatus(order.getStatus());
        this.orderService.handleSaveOrder(orderOld);
        return "redirect:/admin/order";
    }

    @GetMapping("/admin/order/delete/{id}")
    public String getOrderDeletePage(Model model, @PathVariable long id) {
        model.addAttribute("id", id);
        model.addAttribute("newOrder", new Order());
        return "admin/order/delete";
    }

    @PostMapping("/admin/order/delete")
    public String postOrderDetailDelete(@ModelAttribute("newOrder") Order order) {

        this.orderService.handleDeleteOrder(order.getId());
        return "redirect:/admin/order";
    }
}
