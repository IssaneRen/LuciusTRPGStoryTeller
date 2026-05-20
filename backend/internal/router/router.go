package router

import (
	"lucius-trpg/backend/internal/config"
	"lucius-trpg/backend/internal/handler"
	"lucius-trpg/backend/internal/middleware"

	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
)

// Setup configures and returns the Gin router
func Setup(adminUsers []config.AdminUser) *gin.Engine {
	r := gin.Default()

	r.Use(cors.New(cors.Config{
		AllowOrigins:     []string{"http://localhost:5173", "http://localhost:3000"},
		AllowMethods:     []string{"GET", "POST", "PUT", "DELETE", "OPTIONS"},
		AllowHeaders:     []string{"Origin", "Content-Type", "Authorization"},
		AllowCredentials: true,
	}))

	adminAuthHandler := &handler.AdminAuthHandler{AdminUsers: adminUsers}
	userAuthHandler := &handler.UserAuthHandler{}
	graphHandler := &handler.GraphHandler{}

	api := r.Group("/api")
	{
		// Public graph routes
		api.GET("/graphs", graphHandler.ListGraphs)
		api.GET("/graphs/:id", graphHandler.GetGraph)

		// Protected graph write operations (require admin)
		adminGraphs := api.Group("/graphs")
		adminGraphs.Use(middleware.AdminAuth())
		{
			adminGraphs.POST("", graphHandler.CreateGraph)
			adminGraphs.PUT("/:id", graphHandler.UpdateGraph)
			adminGraphs.DELETE("/:id", graphHandler.DeleteGraph)
		}

		// Admin routes
		admin := api.Group("/admin")
		{
			adminAuth := admin.Group("/auth")
			{
				adminAuth.POST("/login", adminAuthHandler.AdminLogin)
				adminAuth.GET("/me", middleware.AdminAuth(), adminAuthHandler.AdminMe)
			}
		}

		// User routes
		user := api.Group("/user")
		{
			userAuth := user.Group("/auth")
			{
				userAuth.POST("/login", userAuthHandler.UserLogin)
				userAuth.GET("/me", middleware.UserAuth(), userAuthHandler.UserMe)
			}
		}
	}

	return r
}
