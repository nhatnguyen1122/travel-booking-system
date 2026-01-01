package edu.hust.travelbookingsystem.service.implementation;

import edu.hust.travelbookingsystem.entity.Order;
import edu.hust.travelbookingsystem.entity.Review;
import edu.hust.travelbookingsystem.entity.User;
import edu.hust.travelbookingsystem.enums.ErrorCode;
import edu.hust.travelbookingsystem.enums.PaymentStatus;
import edu.hust.travelbookingsystem.exception.AppException;
import edu.hust.travelbookingsystem.model.request.ReviewDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.repository.OrderRepository;
import edu.hust.travelbookingsystem.repository.ReviewRepository;
import edu.hust.travelbookingsystem.repository.UserRepository;
import edu.hust.travelbookingsystem.service.ReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReviewServiceImplementation implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Review createReview(Long orderId, Long userId, ReviewDTO reviewDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        // Validate user owns this order
        if (!order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        // Check if order has a hotel
        if (order.getHotel() == null) {
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        // Check if order is paid (completed)
        if (order.getPayment() == null || order.getPayment().getStatus() != PaymentStatus.PAID) {
            throw new AppException(ErrorCode.ORDER_NOT_COMPLETED);
        }

        // Check if review already exists for this order
        if (reviewRepository.existsByOrder_Id(orderId)) {
            throw new AppException(ErrorCode.DUPLICATE_REVIEW);
        }

        // Validate rating
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new AppException(ErrorCode.RATING_NOT_VALID);
        }

        Review review = new Review();
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        review.setUser(user);
        review.setHotel(order.getHotel());
        review.setOrder(order);

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public Review updateReview(Long reviewId, Long userId, ReviewDTO reviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Validate user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        // Validate rating
        if (reviewDTO.getRating() < 1 || reviewDTO.getRating() > 5) {
            throw new AppException(ErrorCode.RATING_NOT_VALID);
        }

        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());

        return reviewRepository.save(review);
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));

        // Validate user owns this review
        if (!review.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED_REVIEW);
        }

        reviewRepository.delete(review);
    }

    @Override
    public PageResponse<?> getReviewsByHotel(Long hotelId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByHotelId(hotelId, pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(reviews.getTotalPages())
                .items(reviews.getContent())
                .build();
    }

    @Override
    public PageResponse<?> getReviewsByUser(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);

        return PageResponse.builder()
                .pageNo(pageNo)
                .pageSize(pageSize)
                .totalPages(reviews.getTotalPages())
                .items(reviews.getContent())
                .build();
    }

    @Override
    public Review getReviewByOrder(Long orderId) {
        return reviewRepository.findByOrder_Id(orderId).orElse(null);
    }

    @Override
    public Double getHotelAverageRating(Long hotelId) {
        Double avg = reviewRepository.calculateAverageRating(hotelId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : null;
    }

    @Override
    public Long getHotelReviewCount(Long hotelId) {
        return reviewRepository.countByHotelId(hotelId);
    }
}
