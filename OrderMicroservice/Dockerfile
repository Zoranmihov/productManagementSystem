# Stage 1: Build dependencies
FROM golang:1.24 AS deps

WORKDIR /app

# Copy mod files separately for layer caching
COPY go.mod go.sum ./
RUN go mod tidy && go mod download

# Stage 2: Dev container with hot reload
FROM golang:1.24

WORKDIR /app

# Install Air globally
RUN go install github.com/air-verse/air@latest

# Copy only validated go.sum from deps stage to avoid checksum issues
COPY --from=deps /app/go.sum ./

# Copy air config
COPY air.toml ./

# Default command: auto-reload with Air
CMD ["air"]
