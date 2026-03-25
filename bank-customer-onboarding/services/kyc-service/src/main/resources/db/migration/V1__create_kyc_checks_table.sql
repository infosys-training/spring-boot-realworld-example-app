CREATE TABLE kyc_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    check_type VARCHAR(30) NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    details TEXT,
    risk_score INTEGER NOT NULL DEFAULT 0,
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_kyc_checks_customer_id ON kyc_checks(customer_id);
CREATE INDEX idx_kyc_checks_status ON kyc_checks(status);
