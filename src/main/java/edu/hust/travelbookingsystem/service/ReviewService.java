package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.entity.Review;
import edu.hust.travelbookingsystem.model.request.ReviewDTO;
import edu.hust.travelbookingsystem.model.response.PageResponse;

public interface ReviewService {

    Review createReview(Long orderId, Long userId, ReviewDTO reviewDTO);

    Review updateReview(Long reviewId, Long userId, ReviewDTO reviewDTO);

    void deleteReview(Long reviewId, Long userId);

    PageResponse<?> getReviewsByHotel(Long hotelId, int pageNo, int pageSize);

    PageResponse<?> getReviewsByUser(Long userId, int pageNo, int pageSize);

    Review getReviewByOrder(Long orderId);

    Double getHotelAverageRating(Long hotelId);

    Long getHotelReviewCount(Long hotelId);
}
