
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       full_name VARCHAR(100) NOT NULL,
                       email VARCHAR(100) UNIQUE NOT NULL,
                       phone_number VARCHAR(20),
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE cards (
                       id BIGSERIAL PRIMARY KEY,
                       encrypted_number VARCHAR(255) UNIQUE NOT NULL,
                       user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                       expiration_date DATE NOT NULL,
                       status VARCHAR(20) NOT NULL,
                       balance NUMERIC(15, 2) NOT NULL DEFAULT 0
);

CREATE TABLE block_requests (
                                id BIGSERIAL PRIMARY KEY,
                                user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
                                card_id BIGINT REFERENCES cards(id) ON DELETE CASCADE,
                                requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                processed BOOLEAN DEFAULT FALSE
);
CREATE TABLE transfer_history (
                                  id BIGSERIAL PRIMARY KEY,
                                  sender_card_id BIGINT REFERENCES cards(id) ON DELETE SET NULL,
                                  receiver_card_id BIGINT REFERENCES cards(id) ON DELETE SET NULL,
                                  amount NUMERIC(15, 2) NOT NULL,
                                  transferred_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_id ON cards(user_id);
CREATE INDEX idx_encrypted_number ON cards(encrypted_number);
CREATE INDEX idx_sender_card ON transfer_history(sender_card_id);
CREATE INDEX idx_receiver_card ON transfer_history(receiver_card_id);