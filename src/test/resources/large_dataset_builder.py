import csv
import random
from pathlib import Path

def generate_big_csv(
    output_path="src/test/resources/stress_test.csv",
    rows=1000,
    columns=["index", "value", "flag"],
    random_data=False
):
    """
    Generates a large CSV file for stress testing
    
    Args:
        output_path: Where to save the CSV
        rows: Number of rows to generate
        columns: List of column headers
        random_data: Whether to fill with random values
    """
    # Ensure directory exists
    Path(output_path).parent.mkdir(parents=True, exist_ok=True)
    
    with open(output_path, 'w', newline='') as f:
        writer = csv.writer(f)
        
        # Write header
        writer.writerow(columns)
        
        # Write data rows
        for i in range(rows):
            if random_data:
                row = [
                    i,  # index
                    round(random.uniform(0, 1000), 2),  # random float
                    random.choice([True, False])  # random boolean
                ]
            else:
                row = [i]  # Simple incrementing index
                
            writer.writerow(row)
    
    print(f"Generated {rows} row CSV at: {output_path}")


if __name__ == "__main__":
    generate_big_csv(rows=1000) 
    generate_big_csv(
        output_path="src/test/resources/stress_test_random.csv",
        rows=5000,
        columns=["id", "temperature", "active", "sensor_id"],
        random_data=True
    )