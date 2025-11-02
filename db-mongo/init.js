db = db.getSiblingDB("mikrodb");

db.createCollection("files");

db.files.insertOne({
  filename: "example.txt",
  uploadedAt: new Date(),
  size: 12345
});

print("MongoDB initialized with sample data!");
