CREATE SEQUENCE meta_files_seq START WITH 1 INCREMENT BY 50;



CREATE TABLE IF NOT EXISTS meta_files (
    id INT PRIMARY KEY DEFAULT nextval('meta_files_seq'),
    name VARCHAR(100) NOT NULL,
    size INTEGER,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_id INTEGER
);
