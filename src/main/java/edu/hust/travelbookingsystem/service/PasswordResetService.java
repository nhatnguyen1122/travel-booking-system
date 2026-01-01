package edu.hust.travelbookingsystem.service;

import edu.hust.travelbookingsystem.model.request.ResetPasswordDTO;

public interface PasswordResetService {

    void requestPasswordReset(String email);

    boolean validateToken(String token);

    void resetPassword(ResetPasswordDTO dto);

    void cleanupExpiredTokens();
}
