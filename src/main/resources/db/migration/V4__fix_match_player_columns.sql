-- Fix player columns in matches table to use VARCHAR instead of BIGINT
-- to match the PlayerEntity userName field type

-- Drop existing foreign key constraints if they exist (including auto-generated ones)
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fklm05xqay0xbc9pd5s1vn7ln64;
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fk1edbfkf47etefpx5artnf2c3j;
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fk_match_player1;
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fk_match_player2;
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fk_match_player3;
ALTER TABLE matches DROP CONSTRAINT IF EXISTS fk_match_player4;

-- Drop any other potential auto-generated constraints
DO $$
DECLARE
    constraint_name TEXT;
BEGIN
    FOR constraint_name IN 
        SELECT conname 
        FROM pg_constraint 
        WHERE conrelid = 'matches'::regclass 
        AND contype = 'f'
    LOOP
        EXECUTE 'ALTER TABLE matches DROP CONSTRAINT IF EXISTS ' || constraint_name;
    END LOOP;
END $$;

-- Clear existing data to avoid type conversion issues
UPDATE matches SET player1_id = NULL, player2_id = NULL, player3_id = NULL, player4_id = NULL;

-- Alter column types from BIGINT to VARCHAR
ALTER TABLE matches 
ALTER COLUMN player1_id TYPE VARCHAR(255),
ALTER COLUMN player2_id TYPE VARCHAR(255),
ALTER COLUMN player3_id TYPE VARCHAR(255),
ALTER COLUMN player4_id TYPE VARCHAR(255);

-- Add foreign key constraints to reference players table
ALTER TABLE matches 
ADD CONSTRAINT fk_match_player1 
FOREIGN KEY (player1_id) REFERENCES players(user_name);

ALTER TABLE matches 
ADD CONSTRAINT fk_match_player2 
FOREIGN KEY (player2_id) REFERENCES players(user_name);

ALTER TABLE matches 
ADD CONSTRAINT fk_match_player3 
FOREIGN KEY (player3_id) REFERENCES players(user_name);

ALTER TABLE matches 
ADD CONSTRAINT fk_match_player4 
FOREIGN KEY (player4_id) REFERENCES players(user_name);
