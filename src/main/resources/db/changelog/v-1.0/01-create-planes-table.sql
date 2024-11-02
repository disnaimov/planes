CREATE TABLE planes (
                        id UUID PRIMARY KEY,
                        capacity INT NOT NULL,
                        plane_type VARCHAR(255) NOT NULL,
                        plane_status VARCHAR(255) NOT NULL,
                        technical_date TIMESTAMP NOT NULL
);