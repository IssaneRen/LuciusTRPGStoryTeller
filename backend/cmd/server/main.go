package main

import (
	"embed"
	"io/fs"
	"log"
	"net/http"
	"os"

	"lucius-trpg/backend/internal/config"
	"lucius-trpg/backend/internal/router"

	"github.com/gin-gonic/gin"
)

//go:embed dist
var frontendFS embed.FS

func main() {
	// Enforce JWT_SECRET in production
	if os.Getenv("GIN_MODE") == "release" && os.Getenv("JWT_SECRET") == "" {
		log.Fatal("JWT_SECRET environment variable is required in production")
	}

	// Ensure data directories exist
	if err := os.MkdirAll("data/graphs", 0755); err != nil {
		log.Fatalf("Failed to create data/graphs directory: %v", err)
	}

	// Parse admin users from environment
	adminUsersRaw := os.Getenv("ADMIN_USERS")
	adminUsers := config.ParseAdminUsers(adminUsersRaw)

	if len(adminUsers) == 0 {
		log.Println("WARNING: No admin users configured. Admin endpoints will not be accessible.")
	} else {
		log.Printf("Loaded %d admin user(s)", len(adminUsers))
	}

	// Setup router
	r := router.Setup(adminUsers)

	// Serve embedded frontend in production
	distFS, err := fs.Sub(frontendFS, "dist")
	if err == nil {
		r.NoRoute(func(c *gin.Context) {
			// Try serving static file first; fallback to index.html for SPA routing
			path := c.Request.URL.Path
			f, e := fs.Stat(distFS, path[1:])
			if e == nil && !f.IsDir() {
				http.FileServer(http.FS(distFS)).ServeHTTP(c.Writer, c.Request)
				return
			}
			c.FileFromFS("/index.html", http.FS(distFS))
		})
	}

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	log.Printf("Server running on :%s", port)
	r.Run(":" + port)
}
