CREATE SEQUENCE meta_files_seq START WITH 1 INCREMENT BY 50;



CREATE TABLE IF NOT EXISTS meta_files (
    id BIGINT PRIMARY KEY DEFAULT nextval('meta_files_seq'),
    file_name VARCHAR(100) NOT NULL,
    file_size INTEGER,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_id VARCHAR(100),
    content_type VARCHAR(100),
    download_count INTEGER NOT NULL DEFAULT 0
);
