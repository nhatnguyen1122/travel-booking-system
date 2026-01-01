package edu.hust.travelbookingsystem.controller;

import edu.hust.travelbookingsystem.entity.Review;
import edu.hust.travelbookingsystem.model.request.ReviewDTO;
import edu.hust.travelbookingsystem.model.response.ApiResponse;
import edu.hust.travelbookingsystem.model.response.PageResponse;
import edu.hust.travelbookingsystem.service.ReviewService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{orderId}/{userId}")
    public ApiResponse<Review> createReview(
            @PathVariable Long orderId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        log.info("Creating review for order {} by user {}", orderId, userId);
        ApiResponse<Review> response = new ApiResponse<>();
        response.setData(reviewService.createReview(orderId, userId, reviewDTO));
        response.setMessage("Review created successfully");
        return response;
    }

    @PutMapping("/{reviewId}/{userId}")
    public ApiResponse<Review> updateReview(
            @PathVariable Long reviewId,
            @PathVariable Long userId,
            @Valid @RequestBody ReviewDTO reviewDTO) {
        log.info("Updating review {} by user {}", reviewId, userId);
        ApiResponse<Review> response = new ApiResponse<>();
        response.setData(reviewService.updateReview(reviewId, userId, reviewDTO));
        response.setMessage("Review updated successfully");
        return response;
    }

    @DeleteMapping("/{reviewId}/{userId}")
    public ApiResponse<Void> deleteReview(
            @PathVariable Long reviewId,
            @PathVariable Long userId) {
        log.info("Deleting review {} by user {}", reviewId, userId);
        reviewService.deleteReview(reviewId, userId);
        ApiResponse<Void> response = new ApiResponse<>();
        response.setMessage("Review deleted successfully");
        return response;
    }

    @GetMapping("/hotel/{hotelId}")
    public ApiResponse<PageResponse> getReviewsByHotel(
            @PathVariable Long hotelId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Getting reviews for hotel {}", hotelId);
        PageResponse<?> reviews = reviewService.getReviewsByHotel(hotelId, pageNo, pageSize);
        return new ApiResponse<>(1000, "get reviews success", reviews);
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<PageResponse> getReviewsByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize) {
        log.info("Getting reviews for user {}", userId);
        PageResponse<?> reviews = reviewService.getReviewsByUser(userId, pageNo, pageSize);
        return new ApiResponse<>(1000, "get reviews success", reviews);
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<Review> getReviewByOrder(@PathVariable Long orderId) {
        log.info("Getting review for order {}", orderId);
        ApiResponse<Review> response = new ApiResponse<>();
        Review review = reviewService.getReviewByOrder(orderId);
        response.setData(review);
        response.setMessage(review != null ? "Review found" : "No review for this order");
        return response;
    }

    @GetMapping("/hotel/{hotelId}/stats")
    public ApiResponse<Map<String, Object>> getHotelReviewStats(@PathVariable Long hotelId) {
        log.info("Getting review stats for hotel {}", hotelId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageRating", reviewService.getHotelAverageRating(hotelId));
        stats.put("reviewCount", reviewService.getHotelReviewCount(hotelId));
        return new ApiResponse<>(1000, "get stats success", stats);
    }
}
