#!/usr/bin/env python3
"""Verify real encrypted session persistence across three Android processes.

First install :data:installDebugAndroidTest. This uses only fixture tokens in the
library test package; it does not read the app's session or contact the server.
"""

import argparse
import re
import subprocess
import uuid


PACKAGE = "com.yapp.plus.data.test"
RUNNER = f"{PACKAGE}/androidx.test.runner.AndroidJUnitRunner"
TEST_CLASS = "com.yapp.plus.data.auth.SessionRestartInstrumentedTest"
PHASES = (
    ("persist", "persistRotatedPair"),
    ("restore", "restoreAndClearPair"),
    ("cleared", "clearedPairStaysEmpty"),
)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--serial", required=True)
    parser.add_argument("--adb", default="adb")
    args = parser.parse_args()
    prefix = [args.adb, "-s", args.serial]
    run_id = uuid.uuid4().hex

    for phase, method in PHASES:
        subprocess.run(prefix + ["shell", "am", "force-stop", PACKAGE], check=True)
        result = subprocess.run(
            prefix + [
                "shell", "am", "instrument", "-w", "-r",
                "-e", "class", f"{TEST_CLASS}#{method}",
                "-e", "sessionRestartPhase", phase,
                "-e", "sessionTestRun", run_id,
                RUNNER,
            ],
            capture_output=True,
            text=True,
            timeout=120,
            check=True,
        )
        if "OK (1 test)" not in result.stdout or "FAILURES!!!" in result.stdout:
            raise RuntimeError(f"{phase} failed:\n{result.stdout}\n{result.stderr}")
        print(f"{phase}: PASS", flush=True)

    logs = subprocess.run(
        prefix + ["logcat", "-d", "-s", "SessionRestartTest:I", "*:S"],
        capture_output=True, text=True, timeout=20, check=True,
    ).stdout
    processes = re.findall(rf"run={run_id} phase=(\w+) pid=(\d+)", logs)
    if len(processes) != 3 or len({pid for _, pid in processes}) != 3:
        raise RuntimeError(f"Expected three distinct processes, observed: {processes}")
    print("Process IDs: " + " -> ".join(pid for _, pid in processes))
    print("Encrypted DataStore restore and cleared-session restore: PASS")


if __name__ == "__main__":
    main()
