package handler

import (
	"net/http"
	"time"

	"lucius-trpg/backend/internal/config"
	"lucius-trpg/backend/internal/util"

	"github.com/gin-gonic/gin"
)

// AdminAuthHandler handles admin authentication
type AdminAuthHandler struct {
	AdminUsers []config.AdminUser
}

type adminLoginRequest struct {
	Username string `json:"username" binding:"required"`
	Password string `json:"password" binding:"required"`
}

// AdminLogin authenticates admin users and issues 24h token
func (h *AdminAuthHandler) AdminLogin(c *gin.Context) {
	var req adminLoginRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"code": 400, "message": err.Error()})
		return
	}

	if !config.CheckAdmin(h.AdminUsers, req.Username, req.Password) {
		c.JSON(http.StatusUnauthorized, gin.H{"code": 401, "message": "invalid credentials"})
		return
	}

	token, err := util.GenerateToken(req.Username, "admin", 24*time.Hour)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"code": 500, "message": "token generation failed"})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code": 200,
		"data": gin.H{"token": token},
	})
}

// AdminMe returns current admin user info from context
func (h *AdminAuthHandler) AdminMe(c *gin.Context) {
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
