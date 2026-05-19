package config

import (
	"crypto/subtle"
	"log"
	"strings"
)

// AdminUser represents an admin user with username and password
type AdminUser struct {
	Username string
	Password string
}

// ParseAdminUsers parses admin users from environment variable format
// Format: "admin:123456,editor:abcdef"
// Returns empty slice if raw is empty
// Fatal logs if format is invalid
func ParseAdminUsers(raw string) []AdminUser {
	if raw == "" {
		return []AdminUser{}
	}

	var users []AdminUser
	pairs := strings.Split(raw, ",")

	for _, pair := range pairs {
		parts := strings.SplitN(strings.TrimSpace(pair), ":", 2)
		if len(parts) != 2 {
			log.Fatalf("Invalid ADMIN_USERS format: %q (expected 'username:password')", pair)
		}

		username := strings.TrimSpace(parts[0])
		password := strings.TrimSpace(parts[1])

		if username == "" || password == "" {
			log.Fatalf("Invalid ADMIN_USERS format: username or password cannot be empty in %q", pair)
		}

		users = append(users, AdminUser{
			Username: username,
			Password: password,
		})
	}

	return users
}

// CheckAdmin verifies admin credentials using constant-time comparison
// Returns true if username and password match any admin user
func CheckAdmin(users []AdminUser, username, password string) bool {
	var found bool
	var validPassword bool

	for _, user := range users {
		// Use constant-time comparison to prevent timing attacks
		usernameMatch := subtle.ConstantTimeCompare([]byte(user.Username), []byte(username)) == 1
		passwordMatch := subtle.ConstantTimeCompare([]byte(user.Password), []byte(password)) == 1

		if usernameMatch {
			found = true
			if passwordMatch {
				validPassword = true
			}
		}
	}

	return found && validPassword
}
