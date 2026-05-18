package main

import (
	"embed"
	"io/fs"
	"log"
	"net/http"
	"os"

	"lucius-trpg/backend/internal/model"
	"lucius-trpg/backend/internal/router"

	"github.com/gin-gonic/gin"
	"github.com/glebarez/sqlite"
	"gorm.io/gorm"
)

//go:embed dist
var frontendFS embed.FS

func main() {
	dbPath := os.Getenv("DB_PATH")
	if dbPath == "" {
		dbPath = "./data/app.db"
	}

	os.MkdirAll("./data", 0755)

	db, err := gorm.Open(sqlite.Open(dbPath), &gorm.Config{})
	if err != nil {
		log.Fatal("failed to connect database:", err)
	}
	db.AutoMigrate(&model.User{})

	r := router.Setup(db)

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
