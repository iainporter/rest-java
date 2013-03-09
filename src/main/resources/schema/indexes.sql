
CREATE UNIQUE INDEX user_uuid_IDX ON rest_user ( uuid);
CREATE INDEX user_email_address_IDX on rest_user (email_address);
CREATE UNIQUE INDEX verification_token_uuid_IDX ON rest_verification_token (uuid);
CREATE UNIQUE INDEX verification_token_token_IDX ON rest_verification_token (token);
CREATE UNIQUE INDEX session_token_last_updated_IDX ON rest_session_token (last_updated);
CREATE UNIQUE INDEX session_token_token_IDX ON rest_session_token (token);
