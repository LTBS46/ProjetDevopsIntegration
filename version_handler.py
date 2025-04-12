from sys import argv

arg = argv[1]

new_val = None
    
with open("version_value", "r") as f:
    ma, mi, rv = map(int, f.read().split("."))
    if arg == "min":
        new_val = f"{ma}.{mi+1}.0"
    elif arg == "maj":
        new_val = f"{ma+1}.0.0"
    elif arg == "rev":
        new_val = f"{ma}.{mi}.{rv+1}"
    else:
        raise ValueError("Wrong argument expected one of 'maj', 'min' or 'rev'")

with open("version_value", "w") as f:
    f.write(new_val)