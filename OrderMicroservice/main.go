package main

import (
	"fmt"
	"log"
	"net/http"
	"ordermicroservice/internal/db"
	"ordermicroservice/internal/routes"
)

func main() {
	// Connect to MongoDB
	if err := db.Connect(); err != nil {
		log.Fatalf("❌ Failed to connect to MongoDB: %v", err)
	}
	fmt.Println("✅ Connected to MongoDB")

	// Setup routes
	router := routes.SetupRouter()

	fmt.Println("🚀 Server running on :8082")
	log.Fatal(http.ListenAndServe(":8082", router))
}
