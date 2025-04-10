package handlers

import (
	"encoding/json"
	"net/http"
)

func TestHandler(w http.ResponseWriter, r *http.Request) {
	response := map[string]string{"message": "It worked"}
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(response)
}
