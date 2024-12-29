package vn.hoidanit.laptopshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Page<Order> getAllOrder(Pageable pageable) {
        return this.orderRepository.findAll(pageable);
    }

    public List<OrderDetail> getOrderDetailList(long id) {
        Order order = this.orderRepository.findById(id);
        return this.orderDetailRepository.findByOrder(order);
    }

    public void handleDeleteOrder(long id) {
        List<OrderDetail> orderDetails = this.getOrderDetailList(id);
        for (OrderDetail orderDetail : orderDetails) {
            this.orderDetailRepository.deleteById(orderDetail.getId());
        }
        this.orderRepository.deleteById(id);
    }

    public Order getAOrder(long id) {
        return this.orderRepository.findById(id);
    }

    public Order handleSaveOrder(Order order) {
        return this.orderRepository.save(order);
    }

    public long countOrders() {
        return this.orderRepository.count();
    }
}
