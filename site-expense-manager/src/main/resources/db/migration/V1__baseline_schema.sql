CREATE TABLE sites (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       site_name VARCHAR(255) NOT NULL,
                       site_code VARCHAR(255) NOT NULL UNIQUE,
                       location VARCHAR(255) NOT NULL,
                       project_manager VARCHAR(255) NOT NULL,
                       budget DECIMAL(15,2) NOT NULL,
                       start_date DATE NOT NULL,
                       end_date DATE NOT NULL,
                       team_size INT NOT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       full_name VARCHAR(255) NOT NULL,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       site_id BIGINT NULL,
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       failed_login_attempts INT DEFAULT 0,
                       locked_until DATETIME NULL,
                       CONSTRAINT fk_users_site FOREIGN KEY (site_id) REFERENCES sites(id)
);

CREATE TABLE travel_expenses (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 travel_code VARCHAR(255) NOT NULL UNIQUE,
                                 site_id BIGINT NOT NULL,
                                 employee_name VARCHAR(255) NOT NULL,
                                 employee_id VARCHAR(255) NOT NULL,
                                 travel_date DATE NOT NULL,
                                 from_location VARCHAR(255) NOT NULL,
                                 to_location VARCHAR(255) NOT NULL,
                                 travel_mode VARCHAR(50) NOT NULL,
                                 travel_cost DECIMAL(10,2) NOT NULL,
                                 travel_purpose VARCHAR(255) NOT NULL,
                                 travel_status VARCHAR(50) NOT NULL,
                                 remarks VARCHAR(255) NULL,
                                 bill_attached BOOLEAN NOT NULL DEFAULT FALSE,
                                 bill_url VARCHAR(500) NULL,
                                 deleted BOOLEAN NOT NULL DEFAULT FALSE,
                                 CONSTRAINT fk_travelexpense_site FOREIGN KEY (site_id) REFERENCES sites(id)
);

CREATE TABLE requests (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          request_code VARCHAR(255) NOT NULL UNIQUE,
                          site_id BIGINT NOT NULL,
                          travel_expense_id BIGINT NULL,
                          requested_by VARCHAR(255) NOT NULL,
                          request_type VARCHAR(50) NOT NULL,
                          description VARCHAR(255) NOT NULL,
                          amount DECIMAL(19,2) NULL,
                          status VARCHAR(50) NULL,
                          approval_stage VARCHAR(50) NULL,
                          approver_name VARCHAR(255) NULL,
                          rejection_reason VARCHAR(255) NULL,
                          request_date DATE NULL,
                          action_date DATE NULL,
                          active BOOLEAN NOT NULL DEFAULT TRUE,
                          deleted BOOLEAN NOT NULL DEFAULT FALSE,
                          CONSTRAINT fk_request_site FOREIGN KEY (site_id) REFERENCES sites(id),
                          CONSTRAINT fk_request_travelexpense FOREIGN KEY (travel_expense_id) REFERENCES travel_expenses(id)
);

CREATE TABLE attendances (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             site_id BIGINT NOT NULL,
                             attendance_date DATE NOT NULL,
                             present_count INT NOT NULL,
                             food_rate_applied DECIMAL(10,2) NOT NULL,
                             total_food_amount DECIMAL(12,2) NOT NULL,
                             deleted BOOLEAN NOT NULL DEFAULT FALSE,
                             CONSTRAINT fk_attendance_site FOREIGN KEY (site_id) REFERENCES sites(id),
                             CONSTRAINT uk_attendance_site_date UNIQUE (site_id, attendance_date)
);

CREATE TABLE ledger (
                        ledger_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        site_id BIGINT NOT NULL,
                        entry_type VARCHAR(50) NOT NULL,
                        source_type VARCHAR(50) NOT NULL,
                        source_id BIGINT NULL,
                        amount DECIMAL(15,2) NOT NULL,
                        description VARCHAR(500) NULL,
                        transaction_date DATE NOT NULL,
                        deleted BOOLEAN NOT NULL DEFAULT FALSE,
                        CONSTRAINT fk_ledger_site FOREIGN KEY (site_id) REFERENCES sites(id)
);

CREATE TABLE system_settings (
                                 id BIGINT PRIMARY KEY,
                                 food_rate_per_person DECIMAL(10,2) NOT NULL,
                                 payout_cycle_days INT NOT NULL
);

INSERT INTO system_settings (id, food_rate_per_person, payout_cycle_days)
VALUES (1, 330, 2);