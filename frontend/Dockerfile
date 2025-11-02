# Stage 1: Build Angular app
FROM node:current-alpine3.22 AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npx ng build --configuration production

# Stage 2: Serve Angular app
FROM node:current-alpine3.22
WORKDIR /app
RUN npm install -g serve
COPY --from=build /app/dist/frontend/browser ./dist
EXPOSE 4200
CMD ["serve", "-s", "dist", "-l", "4200"]

