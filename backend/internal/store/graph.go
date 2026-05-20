package store

import (
	"crypto/rand"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"os"
	"path/filepath"
	"regexp"
	"strings"
)

// Graph represents the full graph data structure
type Graph struct {
	ID    string `json:"id"`
	Name  string `json:"name"`
	Type  string `json:"type"`
	Nodes []Node `json:"nodes"`
	Edges []Edge `json:"edges"`
}

// Node represents a node in the graph
type Node struct {
	ID          string   `json:"id"`
	Label       string   `json:"label"`
	Description string   `json:"description"`
	Tags        []string `json:"tags"`
	X           float64  `json:"x"`
	Y           float64  `json:"y"`
}

// Edge represents an edge in the graph
type Edge struct {
	ID     string `json:"id"`
	Source string `json:"source"`
	Target string `json:"target"`
	Label  string `json:"label"`
}

// GraphListItem represents a graph in the list response
type GraphListItem struct {
	ID        string `json:"id"`
	Name      string `json:"name"`
	Type      string `json:"type"`
	NodeCount int    `json:"nodeCount"`
}

var graphsDir = "data/graphs"

// validIDPattern allows only [a-z0-9-] to prevent path traversal
var validIDPattern = regexp.MustCompile(`^[a-z0-9-]+$`)

// ListGraphs returns all graphs in the data/graphs directory
func ListGraphs() ([]GraphListItem, error) {
	if err := ensureGraphsDir(); err != nil {
		return nil, err
	}

	files, err := os.ReadDir(graphsDir)
	if err != nil {
		return nil, fmt.Errorf("read graphs dir: %w", err)
	}

	var items []GraphListItem
	for _, file := range files {
		if file.IsDir() || !strings.HasSuffix(file.Name(), ".json") {
			continue
		}

		id := strings.TrimSuffix(file.Name(), ".json")
		graph, err := GetGraph(id)
		if err != nil {
			continue // Skip invalid files
		}

		items = append(items, GraphListItem{
			ID:        graph.ID,
			Name:      graph.Name,
			Type:      graph.Type,
			NodeCount: len(graph.Nodes),
		})
	}

	return items, nil
}

// GetGraph reads a single graph file by ID
func GetGraph(id string) (*Graph, error) {
	if !validIDPattern.MatchString(id) {
		return nil, fmt.Errorf("invalid graph ID")
	}

	filePath := filepath.Join(graphsDir, id+".json")
	data, err := os.ReadFile(filePath)
	if err != nil {
		if os.IsNotExist(err) {
			return nil, fmt.Errorf("graph not found")
		}
		return nil, fmt.Errorf("read graph file: %w", err)
	}

	var graph Graph
	if err := json.Unmarshal(data, &graph); err != nil {
		return nil, fmt.Errorf("parse graph JSON: %w", err)
	}

	return &graph, nil
}

// SaveGraph writes a graph to file (full overwrite)
func SaveGraph(id string, graph *Graph) error {
	if !validIDPattern.MatchString(id) {
		return fmt.Errorf("invalid graph ID")
	}

	if err := ensureGraphsDir(); err != nil {
		return err
	}

	// Ensure ID matches
	graph.ID = id

	data, err := json.MarshalIndent(graph, "", "  ")
	if err != nil {
		return fmt.Errorf("marshal graph: %w", err)
	}

	filePath := filepath.Join(graphsDir, id+".json")
	if err := os.WriteFile(filePath, data, 0644); err != nil {
		return fmt.Errorf("write graph file: %w", err)
	}

	return nil
}

// CreateGraph creates a new graph with generated ID
func CreateGraph(name, graphType string) (string, error) {
	if err := ensureGraphsDir(); err != nil {
		return "", err
	}

	// Generate ID: type + "-" + 6 random characters
	id := generateGraphID(graphType)

	graph := &Graph{
		ID:    id,
		Name:  name,
		Type:  graphType,
		Nodes: []Node{},
		Edges: []Edge{},
	}

	if err := SaveGraph(id, graph); err != nil {
		return "", err
	}

	return id, nil
}

// DeleteGraph removes a graph file
func DeleteGraph(id string) error {
	if !validIDPattern.MatchString(id) {
		return fmt.Errorf("invalid graph ID")
	}

	filePath := filepath.Join(graphsDir, id+".json")
	if err := os.Remove(filePath); err != nil {
		if os.IsNotExist(err) {
			return fmt.Errorf("graph not found")
		}
		return fmt.Errorf("delete graph file: %w", err)
	}

	return nil
}

// ensureGraphsDir creates the graphs directory if it doesn't exist
func ensureGraphsDir() error {
	if err := os.MkdirAll(graphsDir, 0755); err != nil {
		return fmt.Errorf("create graphs dir: %w", err)
	}
	return nil
}

// generateGraphID creates a unique ID with format: type-xxxxxx
func generateGraphID(graphType string) string {
	bytes := make([]byte, 3)
	rand.Read(bytes)
	suffix := hex.EncodeToString(bytes)
	return fmt.Sprintf("%s-%s", graphType, suffix)
}
