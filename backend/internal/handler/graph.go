package handler

import (
	"net/http"

	"lucius-trpg/backend/internal/store"

	"github.com/gin-gonic/gin"
)

// GraphHandler handles graph CRUD operations
type GraphHandler struct{}

// ListGraphs returns all graphs
func (h *GraphHandler) ListGraphs(c *gin.Context) {
	items, err := store.ListGraphs()
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "failed to list graphs",
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code": 200,
		"data": items,
	})
}

// GetGraph returns a single graph by ID
func (h *GraphHandler) GetGraph(c *gin.Context) {
	id := c.Param("id")

	graph, err := store.GetGraph(id)
	if err != nil {
		if err.Error() == "graph not found" {
			c.JSON(http.StatusNotFound, gin.H{
				"code":    404,
				"message": "graph not found",
			})
			return
		}
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code": 200,
		"data": graph,
	})
}

// CreateGraphRequest represents the request body for creating a graph
type CreateGraphRequest struct {
	Name string `json:"name" binding:"required"`
	Type string `json:"type" binding:"required"`
}

// CreateGraph creates a new graph
func (h *GraphHandler) CreateGraph(c *gin.Context) {
	var req CreateGraphRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": err.Error(),
		})
		return
	}

	id, err := store.CreateGraph(req.Name, req.Type)
	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{
			"code":    500,
			"message": "failed to create graph",
		})
		return
	}

	c.JSON(http.StatusCreated, gin.H{
		"code": 201,
		"data": gin.H{"id": id},
	})
}

// UpdateGraph saves a complete graph (full overwrite)
func (h *GraphHandler) UpdateGraph(c *gin.Context) {
	id := c.Param("id")

	var graph store.Graph
	if err := c.ShouldBindJSON(&graph); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": err.Error(),
		})
		return
	}

	if err := store.SaveGraph(id, &graph); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "saved",
	})
}

// DeleteGraph deletes a graph
func (h *GraphHandler) DeleteGraph(c *gin.Context) {
	id := c.Param("id")

	if err := store.DeleteGraph(id); err != nil {
		if err.Error() == "graph not found" {
			c.JSON(http.StatusNotFound, gin.H{
				"code":    404,
				"message": "graph not found",
			})
			return
		}
		c.JSON(http.StatusBadRequest, gin.H{
			"code":    400,
			"message": err.Error(),
		})
		return
	}

	c.JSON(http.StatusOK, gin.H{
		"code":    200,
		"message": "deleted",
	})
}
