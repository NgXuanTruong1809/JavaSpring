package vn.hoidanit.laptopshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.hoidanit.laptopshop.domain.Cart;
import vn.hoidanit.laptopshop.domain.CartDetail;
import vn.hoidanit.laptopshop.domain.Order;
import vn.hoidanit.laptopshop.domain.OrderDetail;
import vn.hoidanit.laptopshop.domain.Product;
import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.domain.dto.ProductCriteriaDTO;
import vn.hoidanit.laptopshop.repository.CartDetailRepository;
import vn.hoidanit.laptopshop.repository.CartRepository;
import vn.hoidanit.laptopshop.repository.OrderDetailRepository;
import vn.hoidanit.laptopshop.repository.OrderRepository;
import vn.hoidanit.laptopshop.repository.ProductRepository;
import vn.hoidanit.laptopshop.service.specification.ProductSpecification;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final CartDetailRepository cartDetailRepository;
    private final CartRepository cartRepository;
    private final UserService userService;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public ProductService(ProductRepository productRepository, CartDetailRepository cartDetailRepository,
            CartRepository cartRepository, UserService userService, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.productRepository = productRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    public Product handleSaveProduct(Product newProduct) {
        return this.productRepository.save(newProduct);
    }

    public Page<Product> getAllProductsSpecification(Pageable pageable, ProductCriteriaDTO productCriteriaDTO) {
        if (productCriteriaDTO.getFactory() == null && productCriteriaDTO.getPrice() == null
                && productCriteriaDTO.getTarget() == null) {
            return this.productRepository.findAll(pageable);
        }
        Specification<Product> specification = Specification.where(null);
        if (productCriteriaDTO.getTarget() != null && productCriteriaDTO.getTarget().isPresent()) {
            Specification<Product> curSpecification = ProductSpecification
                    .matchListTarget(productCriteriaDTO.getTarget().get());
            specification = specification.and(curSpecification);
        }
        if (productCriteriaDTO.getFactory() != null && productCriteriaDTO.getFactory().isPresent()) {
            Specification<Product> curSpecification = ProductSpecification
                    .matchListFactory(productCriteriaDTO.getFactory().get());
            specification = specification.and(curSpecification);
        }
        if (productCriteriaDTO.getPrice() != null && productCriteriaDTO.getPrice().isPresent()) {
            Specification<Product> curSpecification = this.builtSpecificationPrice(productCriteriaDTO.getPrice().get());
            specification = specification.and(curSpecification);
        }
        return this.productRepository.findAll(specification, pageable);
    }

    public Specification<Product> builtSpecificationPrice(List<String> price) {
        Specification<Product> combinedSpec = Specification.where(null);
        for (String p : price) {
            double min = 0;
            double max = 0;
            switch (p) {
                case "duoi-10-trieu":
                    min = 0;
                    max = 10000000;
                    break;
                case "10-15-trieu":
                    min = 10000000;
                    max = 15000000;
                    break;
                case "15-20-trieu":
                    min = 15000000;
                    max = 20000000;
                    break;
                case "tren-20-trieu":
                    min = 20000000;
                    max = 0;
                    break;
                // Add more cases as needed
            }
            if (min != 0 && max != 0) {
                Specification<Product> rangeSpec = ProductSpecification.matchMultiplePrice(min, max);
                combinedSpec = combinedSpec.or(rangeSpec);
            } else if (min == 0 && max == 10000000) {
                Specification<Product> rangeSpec = ProductSpecification.maxPrice(max);
                combinedSpec = combinedSpec.or(rangeSpec);
            } else if (min == 20000000 && max == 0) {
                Specification<Product> rangeSpec = ProductSpecification.minPrice(min);
                combinedSpec = combinedSpec.or(rangeSpec);
            }
        }
        return combinedSpec;
    }

    // public Page<Product> getAllProductsSpecification(String name, Pageable
    // pageable) {
    // Specification<Product> specification = ProductSpecification.nameLike(name);
    // return this.productRepository.findAll(specification, pageable);
    // }
    // case 1
    // public Page<Product> getAllProductsSpecification(Pageable page, double min) {
    // return this.productRepository.findAll(ProductSpecs.minPrice(min), page);
    // }
    // case 2
    // public Page<Product> getAllProductsSpecification(Pageable page, double max) {
    // return this.productRepository.findAll(ProductSpecs.maxPrice(max), page);
    // }
    // case 3
    // public Page<Product> getAllProductsSpecification(Pageable page, String
    // factory) {
    // return this.productRepository.findAll(ProductSpecs.matchFactory(factory),
    // page);
    // }
    // case 4
    // public Page<Product> getAllProductsSpecification(Pageable page, List<String>
    // factory) {
    // return this.productRepository.findAll(ProductSpecs.matchListFactory(factory),
    // page);
    // }
    // case 5
    // public Page<Product> getAllProductsSpecification(Pageable page, String price)
    // {
    // // eg: price 10-toi-15-trieu
    // if (price.equals("10-toi-15-trieu")) {
    // double min = 10000000;
    // double max = 15000000;
    // return this.productRepository.findAll(ProductSpecification.matchPrice(min,
    // max),
    // page);
    // } else if (price.equals("15-toi-30-trieu")) {
    // double min = 15000000;
    // double max = 30000000;
    // return this.productRepository.findAll(ProductSpecification.matchPrice(min,
    // max),
    // page);
    // } else
    // return this.productRepository.findAll(page);
    // }
    // case 6
    // public Page<Product> getAllProductsSpecification(Pageable page, List<String>
    // price) {
    // Specification<Product> combinedSpec = (root, query, criteriaBuilder) ->
    // criteriaBuilder.disjunction();
    // int count = 0;
    // for (String p : price) {
    // double min = 0;
    // double max = 0;
    // // Set the appropriate min and max based on the price range string
    // switch (p) {
    // case "10-toi-15-trieu":
    // min = 10000000;
    // max = 15000000;
    // count++;
    // break;
    // case "15-toi-20-trieu":
    // min = 15000000;
    // max = 20000000;
    // count++;
    // break;
    // case "20-toi-30-trieu":
    // min = 20000000;
    // max = 30000000;
    // count++;
    // break;
    // // Add more cases as needed
    // }
    // if (min != 0 && max != 0) {
    // Specification<Product> rangeSpec =
    // ProductSpecification.matchMultiplePrice(min, max);
    // combinedSpec = combinedSpec.or(rangeSpec);
    // }
    // }
    // // Check if any price ranges were added (combinedSpec is empty)
    // if (count == 0) {
    // return this.productRepository.findAll(page);
    // }
    // return this.productRepository.findAll(combinedSpec, page);
    // }

    public Page<Product> getAllProducts(Pageable pageable) {
        return this.productRepository.findAll(pageable);
    }

    public List<Product> getAllProducts() {
        return this.productRepository.findAll();
    }

    public Product getProductById(long id) {
        return this.productRepository.findById(id);
    }

    public String getImageById(long id) {
        Product product = getProductById(id);
        return product.getImage();
    }

    public void deleteAProduct(long id) {
        this.productRepository.deleteById(id);
    }

    public void handleAddProductToCart(long id, String email, HttpSession session) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                // Create new cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);

                cart = this.cartRepository.save(otherCart);
            }
            // save cart_detail
            Product product = this.productRepository.findById(id);
            // check card detail Exist product
            CartDetail cartOld = this.cartDetailRepository.findByCartAndProduct(cart, product);
            if (cartOld == null) {
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setPrice(product.getPrice());
                cartDetail.setProduct(product);
                cartDetail.setQuantity(1);

                this.cartDetailRepository.save(cartDetail);

                // update sum of Cart (sum distinct Product)
                int sum = cart.getSum() + 1;
                cart.setSum(sum);
                session.setAttribute("sum", sum);
                cart = this.cartRepository.save(cart);
            } else {
                cartOld.setQuantity(cartOld.getQuantity() + 1);
                this.cartDetailRepository.save(cartOld);
            }

        }

    }

    public void handleRemoveCardDetails(long id, HttpSession session) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(id);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart curCart = cartDetail.getCart();
            this.cartDetailRepository.deleteById(id);

            if (curCart.getSum() > 1) {
                int s = curCart.getSum() - 1;
                curCart.setSum(s);
                session.setAttribute("sum", s);
            } else {
                this.cartRepository.deleteById(curCart.getId());
                session.setAttribute("sum", 0);
            }
        }
    }

    public void handleCheckOut(List<Long> id, List<Long> quantity) {
        for (int i = 0; i < id.size(); i++) {
            Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(id.get(i));
            if (cartDetailOptional.isPresent()) {
                CartDetail cartDetail = cartDetailOptional.get();
                cartDetail.setQuantity(quantity.get(i));
                this.cartDetailRepository.save(cartDetail);
            }
        }
    }

    public void handleOrder(User user, HttpSession session, String name, String address, String phone,
            String totalAmount) {
        Cart cart = this.cartRepository.findByUser(user);
        if (cart == null) {
            return;
        }
        List<CartDetail> cartDetails = cart.getCartDetails();

        // create order
        Order order = new Order();
        order.setUser(user);
        order.setReceiverName(name);
        order.setReceiverAddress(address);
        order.setReceiverPhone(phone);
        order.setStatus("PENDING");
        order.setTotalPrice(Double.parseDouble(totalAmount));
        order = this.orderRepository.save(order); // save to have order id

        // create order detail

        for (CartDetail cartDetail : cartDetails) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(cartDetail.getProduct());
            orderDetail.setPrice(cartDetail.getPrice());
            orderDetail.setQuantity(cartDetail.getQuantity());
            this.orderDetailRepository.save(orderDetail);
        }

        for (CartDetail cartDetail : cartDetails) {
            this.cartDetailRepository.deleteById(cartDetail.getId());
        }

        this.cartRepository.deleteById(cart.getId());
        // update session
        session.setAttribute("sum", 0);

    }

    public long countProducts() {
        return this.productRepository.count();
    }

    public void handleAddProductToCartFromDetail(long id, String email, HttpSession session, long quantity) {
        User user = this.userService.getUserByEmail(email);
        if (user != null) {
            Cart cart = this.cartRepository.findByUser(user);
            if (cart == null) {
                // Create new cart
                Cart otherCart = new Cart();
                otherCart.setUser(user);
                otherCart.setSum(0);

                cart = this.cartRepository.save(otherCart);
            }
            // save cart_detail
            Product product = this.productRepository.findById(id);
            // check card detail Exist product
            CartDetail cartOld = this.cartDetailRepository.findByCartAndProduct(cart, product);
            if (cartOld == null) {
                CartDetail cartDetail = new CartDetail();
                cartDetail.setCart(cart);
                cartDetail.setPrice(product.getPrice());
                cartDetail.setProduct(product);
                cartDetail.setQuantity(quantity);

                this.cartDetailRepository.save(cartDetail);

                // update sum of Cart (sum distinct Product)
                int sum = cart.getSum() + 1;
                cart.setSum(sum);
                session.setAttribute("sum", sum);
                cart = this.cartRepository.save(cart);
            } else {
                cartOld.setQuantity(cartOld.getQuantity() + quantity);
                this.cartDetailRepository.save(cartOld);
            }

        }

    }

}
