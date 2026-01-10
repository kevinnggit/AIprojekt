# --- Build Stage ---
FROM node:20-alpine AS build
WORKDIR /app

# Build-Args von docker-compose
ARG VITE_JAVA_API_URL
ARG VITE_PYTHON_API_URL

# Als ENV setzen, damit Vite sie beim Build liest
ENV VITE_JAVA_API_URL=${VITE_JAVA_API_URL}
ENV VITE_PYTHON_API_URL=${VITE_PYTHON_API_URL}

COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# --- Runtime Stage (NGINX) ---
FROM nginx:alpine
# SPA-Fallback für client-side routing
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80