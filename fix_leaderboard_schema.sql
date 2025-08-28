-- Manual fix for leaderboard table player_id column type mismatch
-- This script fixes the issue where player_id is bigint but should be varchar

-- Step 1: Check current data and backup if needed
-- SELECT * FROM leaderboard LIMIT 5;

-- Step 2: Drop foreign key constraint if it exists
ALTER TABLE leaderboard DROP CONSTRAINT IF EXISTS fk_leaderboard_player;

-- Step 3: Drop the existing player_id column (this will lose existing data)
-- WARNING: This will delete existing leaderboard data
ALTER TABLE leaderboard DROP COLUMN IF EXISTS player_id;

-- Step 4: Add the new player_id column as varchar
ALTER TABLE leaderboard ADD COLUMN player_id VARCHAR(255) NOT NULL;

-- Step 5: Add unique constraint
ALTER TABLE leaderboard ADD CONSTRAINT uk_leaderboard_player_id UNIQUE (player_id);

-- Step 6: Add foreign key constraint to reference players.user_name
ALTER TABLE leaderboard ADD CONSTRAINT fk_leaderboard_player 
    FOREIGN KEY (player_id) REFERENCES players(user_name);

-- Step 7: Create index for better performance
CREATE INDEX IF NOT EXISTS idx_leaderboard_player_id ON leaderboard(player_id);

-- Step 8: Insert sample data (optional - adjust as needed)
-- INSERT INTO leaderboard (player_id, ranking, matches_played, matches_won, matches_lost, points, last_updated)
-- SELECT user_name, 1, 0, 0, 0, 1000, NOW()
-- FROM players
-- WHERE NOT EXISTS (SELECT 1 FROM leaderboard WHERE player_id = players.user_name);

COMMIT;
