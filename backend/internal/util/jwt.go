package util

import (
	"errors"
	"log"
	"os"
	"time"

	"github.com/golang-jwt/jwt/v5"
)

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

func GenerateToken(userID uint) (string, error) {
	claims := jwt.MapClaims{
		"user_id": userID,
		"exp":     time.Now().Add(24 * time.Hour).Unix(),
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	return token.SignedString(getSecret())
}

func ParseToken(tokenStr string) (uint, error) {
	token, err := jwt.Parse(tokenStr, func(t *jwt.Token) (interface{}, error) {
		return getSecret(), nil
	}, jwt.WithValidMethods([]string{"HS256"}))
	if err != nil {
		return 0, err
	}
	claims, ok := token.Claims.(jwt.MapClaims)
	if !ok {
		return 0, errors.New("invalid claims")
	}
	userID, ok := claims["user_id"].(float64)
	if !ok {
		return 0, errors.New("invalid user_id claim")
	}
	return uint(userID), nil
}
