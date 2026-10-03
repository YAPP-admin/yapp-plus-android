#!/usr/bin/env python3
"""Install this repository's Codex slash prompts into the local Codex home."""

import argparse
import os
from pathlib import Path
import shutil


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--force", action="store_true", help="update installed YAPP prompts")
    args = parser.parse_args()

    root = Path(__file__).resolve().parents[1]
    source_dir = root / ".codex" / "prompts"
    codex_home = Path(os.environ.get("CODEX_HOME", Path.home() / ".codex")).expanduser()
    target_dir = codex_home / "prompts"
    target_dir.mkdir(parents=True, exist_ok=True)

    for source in sorted(source_dir.glob("yapp-*.md")):
        target = target_dir / source.name
        if target.exists() and target.read_bytes() != source.read_bytes() and not args.force:
            raise SystemExit(f"Existing prompt differs: {target}. Re-run with --force to update it.")
        shutil.copy2(source, target)
        print(target)


if __name__ == "__main__":
    main()
