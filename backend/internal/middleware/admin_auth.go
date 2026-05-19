package middleware

import (
	"net/http"
	"strings"

	"lucius-trpg/backend/internal/util"

	"github.com/gin-gonic/gin"
)

// AdminAuth validates JWT and checks for admin role
func AdminAuth() gin.HandlerFunc {
	return func(c *gin.Context) {
		header := c.GetHeader("Authorization")
		if header == "" {
			c.JSON(http.StatusUnauthorized, gin.H{"code": 401, "message": "missing token"})
			c.Abort()
			return
		}

		parts := strings.SplitN(header, " ", 2)
		if len(parts) != 2 || parts[0] != "Bearer" {
			c.JSON(http.StatusUnauthorized, gin.H{"code": 401, "message": "invalid token format"})
			c.Abort()
			return
		}

		claims, err := util.ParseToken(parts[1])
		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"code": 401, "message": "invalid token"})
			c.Abort()
			return
		}

		if claims.Role != "admin" {
			c.JSON(http.StatusForbidden, gin.H{"code": 403, "message": "admin access required"})
			c.Abort()
			return
		}

		c.Set("claims", claims)
		c.Next()
	}
}
