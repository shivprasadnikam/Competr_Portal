-- Fix leaderboard table player_id column type mismatch
-- Change from bigint to varchar to match player usernames

-- First, drop the foreign key constraint if it exists
ALTER TABLE leaderboard DROP CONSTRAINT IF EXISTS fk_leaderboard_player;

-- Drop the existing player_id column
ALTER TABLE leaderboard DROP COLUMN IF EXISTS player_id;

-- Add the new player_id column as varchar
ALTER TABLE leaderboard ADD COLUMN player_id VARCHAR(255) NOT NULL UNIQUE;

-- Add foreign key constraint to reference players.user_name
ALTER TABLE leaderboard ADD CONSTRAINT fk_leaderboard_player 
    FOREIGN KEY (player_id) REFERENCES players(user_name);

-- Create index for better performance
CREATE INDEX IF NOT EXISTS idx_leaderboard_player_id ON leaderboard(player_id);
