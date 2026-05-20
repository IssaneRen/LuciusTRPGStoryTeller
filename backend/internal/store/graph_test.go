package store

import (
	"os"
	"path/filepath"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestGraphOperations(t *testing.T) {
	// Use a temporary directory for tests
	originalDir := graphsDir
	graphsDir = filepath.Join(os.TempDir(), "test-graphs")
	defer func() {
		os.RemoveAll(graphsDir)
		graphsDir = originalDir
	}()

	// Test CreateGraph
	id, err := CreateGraph("Test Graph", "clue")
	assert.NoError(t, err)
	assert.NotEmpty(t, id)
	assert.Contains(t, id, "clue-")

	// Test GetGraph
	graph, err := GetGraph(id)
	assert.NoError(t, err)
	assert.Equal(t, id, graph.ID)
	assert.Equal(t, "Test Graph", graph.Name)
	assert.Equal(t, "clue", graph.Type)
	assert.Empty(t, graph.Nodes)
	assert.Empty(t, graph.Edges)

	// Test SaveGraph with nodes and edges
	graph.Nodes = []Node{
		{ID: "n1", Label: "Node 1", Description: "First node", Tags: []string{"test"}, X: 0, Y: 0},
	}
	graph.Edges = []Edge{
		{ID: "e1", Source: "n1", Target: "n2", Label: "edge"},
	}
	err = SaveGraph(id, graph)
	assert.NoError(t, err)

	// Verify the save
	savedGraph, err := GetGraph(id)
	assert.NoError(t, err)
	assert.Len(t, savedGraph.Nodes, 1)
	assert.Len(t, savedGraph.Edges, 1)
	assert.Equal(t, "Node 1", savedGraph.Nodes[0].Label)

	// Test ListGraphs
	items, err := ListGraphs()
	assert.NoError(t, err)
	assert.Len(t, items, 1)
	assert.Equal(t, id, items[0].ID)
	assert.Equal(t, 1, items[0].NodeCount)

	// Test DeleteGraph
	err = DeleteGraph(id)
	assert.NoError(t, err)

	// Verify deletion
	_, err = GetGraph(id)
	assert.Error(t, err)

	// Test invalid ID validation
	_, err = GetGraph("../etc/passwd")
	assert.Error(t, err)
	assert.Contains(t, err.Error(), "invalid graph ID")
}
