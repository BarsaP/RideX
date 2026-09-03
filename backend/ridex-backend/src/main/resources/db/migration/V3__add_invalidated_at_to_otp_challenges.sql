ALTER TABLE otp_challenges
     ADD COLUMN invalidated_at TIMESTAMPTZ;

CREATE INDEX idx_otp_challenges_active
     ON otp_challenges(phone, purpose, created_at DESC)
     WHERE verified_at IS NULL AND invalidated_at IS NULL;