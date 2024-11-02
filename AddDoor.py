import os
import argparse
import json

# Define the paths to the files
files = [
    "common/src/main/resources/assets/horizontaldoors/blockstates/horizontal_oak_door.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_bottom_side_left.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_bottom_side_right.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_bottom_up_left.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_bottom_up_right.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_top_side_left.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_top_side_right.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_top_up_left.json",
    "common/src/main/resources/assets/horizontaldoors/models/block/horizontal_oak_door_top_up_right.json",
    "common/src/main/resources/assets/horizontaldoors/models/item/horizontal_oak_door.json",
    "common/src/main/resources/data/horizontaldoors/recipes/horizontal_oak_door.json",
    "common/src/main/resources/data/horizontaldoors/recipes/oak_door.json",
    "common/src/main/resources/data/horizontaldoors/advancements/recipes/building_blocks/horizontal_oak_door.json",
    "common/src/main/resources/data/horizontaldoors/loot_tables/blocks/horizontal_oak_door.json"
]

lang_en_us_file = "common/src/main/resources/assets/horizontaldoors/lang/en_us.json"

# Function to replace text in file and save as a new file with updated name
def process_file(original_file_path, name):
    # Replace placeholders in the file path
    new_file_path = original_file_path.replace("oak_door", name)
    
    # Create directories if they don't exist
    os.makedirs(os.path.dirname(new_file_path), exist_ok=True)
    
    # Read original file content
    with open(original_file_path, 'r') as file:
        content = file.read()
    
    # Replace placeholders in the file content
    new_content = content.replace("oak_door", name)
    
    # Write the new content to the new file
    with open(new_file_path, 'w') as new_file:
        new_file.write(new_content)
    
    print(f"Processed file for {name}: {new_file_path}")

# Main function to process the list of names
def process_names(names):

    #lang
    with open(lang_en_us_file, 'r') as file:
        json_data = file.read()
    data = json.loads(json_data)

    for name in names:
        n = name.lower()
        data["block.horizontaldoors.horizontal_" + n] = "Horizontal " + n.replace("_", " ").title()
        for file_path in files:
            process_file(file_path, n)

    with open(lang_en_us_file, 'w') as new_file:
            new_file.write(json.dumps(data, indent=4))

if __name__ == "__main__":
    # Set up argument parser
    parser = argparse.ArgumentParser(description="Create JSON files by using horizontal_oak_door as template. (does not modify tags) (Example: python AddDoor.py spruce_door birch_door)")
    
    # Define a positional argument for the list of names
    parser.add_argument('names', nargs='+', help="List of names to process (e.g., Birch Spruce Jungle)")

    # Parse the arguments
    args = parser.parse_args()

    # Process each name provided in the command-line arguments
    process_names(args.names)