package util

import (
	"errors"
	"log"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

// UserClaims represents JWT claims with username and role
type UserClaims struct {
	Username string `json:"username"`
	Role     string `json:"role"`
	jwt.RegisteredClaims
}

// getSecret retrieves JWT secret from environment
// Falls back to "dev-secret" in development
// Fails fast in production if JWT_SECRET is not set
func getSecret() []byte {
	s := os.Getenv("JWT_SECRET")
	if s == "" {
		if os.Getenv("GIN_MODE") == "release" {
			log.Fatal("JWT_SECRET is required in production")
		}
		s = "dev-secret"
	}
	return []byte(s)
}

// GenerateToken creates a JWT token with username and role
func GenerateToken(username, role string, duration time.Duration) (string, error) {
	claims := UserClaims{
		Username: username,
		Role:     role,
		RegisteredClaims: jwt.RegisteredClaims{
			ExpiresAt: jwt.NewNumericDate(time.Now().Add(duration)),
			IssuedAt:  jwt.NewNumericDate(time.Now()),
		},
	}

	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(getSecret())
}

// ParseToken parses and validates a JWT token string
// Returns UserClaims containing username and role
func ParseToken(tokenStr string) (*UserClaims, error) {
	token, err := jwt.ParseWithClaims(tokenStr, &UserClaims{}, func(t *jwt.Token) (interface{}, error) {
		return getSecret(), nil
	}, jwt.WithValidMethods([]string{"HS256"}))

	if err != nil {
		return nil, err
	}

	if !token.Valid {
		return nil, errors.New("invalid token")
	}

	claims, ok := token.Claims.(*UserClaims)
	if !ok {
		return nil, errors.New("invalid claims type")
	}

	return claims, nil
}
