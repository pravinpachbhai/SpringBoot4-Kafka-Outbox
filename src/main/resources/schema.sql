CREATE TABLE products (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(50) NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          price DECIMAL(10,2) NOT NULL,
                          created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          UNIQUE (code)
);

CREATE TABLE outbox_event (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              aggregate_type VARCHAR(100) NOT NULL,
                              aggregate_id VARCHAR(100) NOT NULL,
                              topic VARCHAR(255) NOT NULL,
                              message_key VARCHAR(255) NOT NULL,
                              event_id VARCHAR(255) NOT NULL,
                              UNIQUE (event_id),
                              event_type VARCHAR(255) NOT NULL,
                              last_error TEXT,
                              payload TEXT NOT NULL,
                              status VARCHAR(20) NOT NULL, -- PENDING, SENT, FAILED
                              retry_count INT DEFAULT 0,
                              next_retry_time TIMESTAMP NULL,
                              processed_at TIMESTAMP NULL,
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_outbox_status_created
    ON outbox_event(status, created_at);

CREATE INDEX idx_outbox_retry
    ON outbox_event(status, next_retry_time);

CREATE TABLE kafka_failed_messages (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       topic VARCHAR(255),
                                       message JSON,
                                       error TEXT,
                                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
