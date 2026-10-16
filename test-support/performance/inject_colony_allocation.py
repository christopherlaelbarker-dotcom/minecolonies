#!/usr/bin/env python3
"""Replace Java mock markers with selected *production source* methods."""
from pathlib import Path
import re
import sys

if len(sys.argv) != 5:
    raise SystemExit("Usage: inject_colony_allocation.py CitizenManager.java AnimalManager.java Template.java Output.java")
citizen = Path(sys.argv[1]).read_text()
animal = Path(sys.argv[2]).read_text()
template = Path(sys.argv[3]).read_text()

def method(source, name, ret):
    # Keep production implementation, including any future changes.
    occurrences = list(re.finditer(r"public\s+" + re.escape(ret) + r"\s+" + re.escape(name) + r"\s*\(", source))
    if len(occurrences) != 1:
        raise ValueError("Expected one " + name + " declaration, found " + str(len(occurrences)))
    start = occurrences[0].start()
    brace = source.find("{", start)
    depth = 0
    for i in range(brace, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                return source[start:i + 1]
    raise ValueError("Unclosed method " + name)

for key, source, name, ret in (
    ("PROD_CITIZEN_TICK", citizen, "tickCitizenData", "boolean"),
    ("PROD_CITIZEN_RANDOM", citizen, "getRandomCitizen", "ICitizenData"),
    ("PROD_ANIMAL_TICK", animal, "tickAnimalData", "boolean"),
):
    token = "// " + key
    if template.count(token) != 1:
        raise ValueError("Missing unique marker: " + token)
    template = template.replace(token, method(source, name, ret))

Path(sys.argv[4]).write_text(template)
