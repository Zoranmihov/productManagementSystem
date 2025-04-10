package routes

import (
	"ordermicroservice/internal/handlers"

	"github.com/go-chi/chi/v5"
)

func SetupRouter() *chi.Mux {
	r := chi.NewRouter()

	// Group routes under /api
	r.Route("/api/order1", func(r chi.Router) {
		r.Get("/test", handlers.TestHandler)
	})

	return r
}
