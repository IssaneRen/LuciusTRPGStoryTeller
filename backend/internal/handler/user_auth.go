package handler

import (
	"net/http"
	"time"
	"unicode/utf8"

	"lucius-trpg/backend/internal/util"

	"github.com/gin-gonic/gin"
)

// UserAuthHandler handles user authentication
type UserAuthHandler struct{}

type userLoginRequest struct {
	Username string `json:"username" binding:"required"`
}

// UserLogin authenticates users with username only and issues 7-day token
func (h *UserAuthHandler) UserLogin(c *gin.Context) {
	var req userLoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": 400, "message": err.Error()})
		return
	}

	// Validate username length (2-32 characters)
	length := utf8.RuneCountInString(req.Username)
	if length < 2 || length > 32 {
		c.JSON(http.StatusBadRequest, gin.H{"code": 400, "message": "username must be 2-32 characters"})
		return
	}

	token, err := util.GenerateToken(req.Username, "user", 7*24*time.Hour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"code": 500, "message": "token generation failed"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code": 200,
		"data": gin.H{"token": token},
	})
}

// UserMe returns current user info from context
func (h *UserAuthHandler) UserMe(c *gin.Context) {
	claims, exists := c.Get("claims")
	if !exists {
		c.JSON(http.StatusUnauthorized, gin.H{"code": 401, "message": "no claims found"})
		return
	}

	userClaims, ok := claims.(*util.UserClaims)
	if !ok {
		c.JSON(http.StatusInternalServerError, gin.H{"code": 500, "message": "invalid claims"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code": 200,
		"data": gin.H{
			"username": userClaims.Username,
			"role":     userClaims.Role,
		},
	})
}
