#!/usr/bin/env python3
"""Insert the real Tree.java methods into Java world mocks to run regression cases."""
from pathlib import Path
import re
import sys

source = Path(sys.argv[1]).read_text()
template = Path(sys.argv[2]).read_text()

def method(name):
    found = list(re.finditer(r"private static boolean\s+" + re.escape(name) + r"\s*\(", source))
    if len(found) != 1:
        raise RuntimeError(f"Expected exactly one production {name} method, found {len(found)}")
    start = found[0].start()
    brace = source.index("{", start)
    depth = 0
    for index in range(brace, len(source)):
        if source[index] == "{":
            depth += 1
        elif source[index] == "}":
            depth -= 1
            if depth == 0:
                return source[start:index + 1]
    raise RuntimeError("Method has no closing brace: " + name)

for tag, name in (("PRODUCTION_LEAF_SCAN", "hasEnoughLeavesAndIsSupposedToCut"),
                  ("PRODUCTION_SAPLING_CHECK", "supposedToCut")):
    needle = "// " + tag
    if template.count(needle) != 1:
        raise RuntimeError("Missing unique template marker " + needle)
    template = template.replace(needle, method(name))

Path(sys.argv[3]).write_text(template)
